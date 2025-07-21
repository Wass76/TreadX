package com.TreadX.district.sales.service;

import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final LeadsRepository leadsRepository;

    /**
     * Get file resource for download (forces download)
     */
    public Resource getFileForDownload(Long leadId) {
        Leads lead = getLeadEntityById(leadId);
        validateFileExists(lead);
        
        Path filePath = Paths.get(lead.getUploadedFile());
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        }
        catch (MalformedURLException e) {
            throw new ConflictException(e.getMessage());
        }
        
        validateResource(resource);
        return resource;
    }

    /**
     * Get file resource for preview (displays in browser)
     */
    public Resource getFileForPreview(Long leadId) {
        Leads lead = getLeadEntityById(leadId);
        validateFileExists(lead);
        
        Path filePath = Paths.get(lead.getUploadedFile());
        Resource resource;
        try {
           resource = new UrlResource(filePath.toUri());
        }
        catch (MalformedURLException e) {
            throw new ConflictException(e.getMessage());
        }

        validateResource(resource);
        return resource;
    }

    /**
     * Get content type for file preview
     */
    public MediaType getContentType(Long leadId) {
        Leads lead = getLeadEntityById(leadId);
        validateFileExists(lead);
        
        Path filePath = Paths.get(lead.getUploadedFile());
        String contentType = determineContentType(filePath);
        
        return MediaType.parseMediaType(contentType);
    }

    /**
     * Get download headers with filename
     */
    public HttpHeaders getDownloadHeaders(Long leadId) {
        Leads lead = getLeadEntityById(leadId);
        validateFileExists(lead);
        
        Path filePath = Paths.get(lead.getUploadedFile());
        String filename = filePath.getFileName().toString();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        
        return headers;
    }

    /**
     * Get lead entity by ID (internal method to avoid circular dependency)
     */
    private Leads getLeadEntityById(Long id) {
        return leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
    }

    /**
     * Save uploaded file for a lead
     */
    public String saveLeadFile(MultipartFile file) {
        try {
            // Use project root as base directory
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "leads" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + fileName;
            file.transferTo(new File(filePath));
            
            log.info("File saved successfully: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to save uploaded file: {}", e.getMessage());
            throw new RuntimeException("Failed to save uploaded file", e);
        }
    }

    /**
     * Delete file for a lead
     */
    public void deleteLeadFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("File deleted successfully: {}", filePath);
                    } else {
                        log.warn("Failed to delete file: {}", filePath);
                    }
                }
            } catch (Exception e) {
                log.error("Error deleting file {}: {}", filePath, e.getMessage());
            }
        }
    }

    /**
     * Validate file upload
     */
    public void validateFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        
        // Check file size (e.g., max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 10MB");
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "pdf", "txt", "doc", "docx", "xls", "xlsx"};
        
        boolean isValidExtension = false;
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equals(extension)) {
                isValidExtension = true;
                break;
            }
        }
        
        if (!isValidExtension) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + String.join(", ", allowedExtensions));
        }
    }

    /**
     * Validate that file exists for the lead
     */
    private void validateFileExists(Leads lead) {
        if (lead.getUploadedFile() == null || lead.getUploadedFile().isEmpty()) {
            throw new ResourceNotFoundException("No file uploaded for this lead");
        }
    }

    /**
     * Validate that resource exists and is readable
     */
    private void validateResource(Resource resource) {
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("File not found or not readable");
        }
    }

    /**
     * Determine content type based on file extension
     */
    private String determineContentType(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".csv")) {
            return "text/csv";
        } else if (fileName.endsWith(".xml")) {
            return "application/xml";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else {
            // Default to octet-stream for unknown types
            return "application/octet-stream";
        }
    }
} 