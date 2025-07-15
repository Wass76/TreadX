package com.TreadX.district.sales.service;

import com.TreadX.district.sales.dto.LeadsRequestDTO;
import com.TreadX.district.sales.dto.LeadsResponseDTO;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.mapper.LeadsMapper;
import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.enums.LeadStatus;
import com.TreadX.user.service.AuthorizationService;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

import static com.TreadX.dealers.enums.LeadStatus.PENDING;
import com.TreadX.district.sales.dto.LeadValidationRequest;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import java.security.Principal;

@Service
public class LeadsService {
    @Autowired
    private LeadsRepository leadsRepository;
    @Autowired
    private LeadsMapper leadsMapper;
    @Autowired
    private com.TreadX.dealers.repository.DealerRepository dealerRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public LeadsResponseDTO createLead(LeadsRequestDTO request, MultipartFile file) {
        if (leadsRepository.existsDuplicateLead(request.getBusinessName(), request.getStreetNumber(), request.getPostalCode(), request.getPhoneNumber())) {
            throw new ConflictException("A lead with the same business name, street number, postal code, and phone number already exists.");
        }
        Leads leads = leadsMapper.toEntity(request);
        // Handle file upload
        if (file != null && !file.isEmpty()) {
            String filePath = saveLeadFile(file);
            leads.setUploadedFile(filePath);
        }
        if (request.getDealerId() != null) {
            Dealer dealer = dealerRepository.findById(request.getDealerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getDealerId()));
            leads.setDealer(dealer);
        }
        leads.setStatus(PENDING);
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO updateLead(Long id, LeadsRequestDTO request, MultipartFile file) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        // Status transition validation
        if (request.getStatus() != null && !request.getStatus().equals(leads.getStatus())) {
            validateStatusTransition(leads.getStatus(), request.getStatus());
            // Set validation tracking fields if status is being approved or denied
            if (request.getStatus() == LeadStatus.APPROVED || request.getStatus() == LeadStatus.DENIED) {
                // You may want to get the current user from the security context or session
                // For now, set to null or implement user fetching logic
                leads.setValidatedBy(authorizationService.getCurrentUser());
                leads.setValidatedAt(java.time.LocalDateTime.now());
            }
        }
        leadsMapper.updateEntityFromRequest(leads, request);
        if (file != null && !file.isEmpty()) {
            String filePath = saveLeadFile(file);
            leads.setUploadedFile(filePath);
        }
        if (request.getDealerId() != null) {
            Dealer dealer = dealerRepository.findById(request.getDealerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getDealerId()));
            leads.setDealer(dealer);
        }
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    public LeadsResponseDTO getLeadById(Long id) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        return leadsMapper.toResponse(leads);
    }

    public Page<LeadsResponseDTO> getAllLeads(Pageable pageable) {
        return leadsRepository.findAll(pageable)
                .map(leadsMapper::toResponse);
    }

    public List<LeadsResponseDTO> getLeadsByDealer(Long dealerId) {
        return leadsRepository.findAll().stream()
                .filter(lead -> lead.getDealer() != null && lead.getDealer().getId().equals(dealerId))
                .map(leadsMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<LeadsResponseDTO> getLeadsByStatus(LeadStatus status, Pageable pageable) {
        return leadsRepository.findByStatus(status, pageable)
                .map(leadsMapper::toResponse);
    }

    @Transactional
    public void deleteLead(Long id) {
        if (!leadsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lead not found with id: " + id);
        }
        leadsRepository.deleteById(id);
    }

    @Transactional
    public LeadsResponseDTO validateLead(Long id, LeadValidationRequest request, Principal principal) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        validateStatusTransition(leads.getStatus(), request.getStatus());
        leads.setStatus(request.getStatus());
        leads.setNotes(request.getNotes());
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            leads.setValidatedBy(user);
        }
        leads.setValidatedAt(java.time.LocalDateTime.now());
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    private String saveLeadFile(MultipartFile file) {
        try {
            // Use project root as base directory
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "leads" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String filePath = uploadDir + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save uploaded file", e);
        }
    }

    private void validateStatusTransition(LeadStatus currentStatus, LeadStatus newStatus) {
        if (currentStatus == LeadStatus.PENDING && (newStatus == LeadStatus.APPROVED || newStatus == LeadStatus.DENIED)) return;
        if (currentStatus == LeadStatus.DENIED && newStatus == LeadStatus.PENDING) return; // allow re-validation
        throw new ConflictException("Invalid status transition from " + currentStatus + " to " + newStatus);
    }
} 