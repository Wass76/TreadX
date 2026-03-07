package com.TreadX.file.mapper;

import com.TreadX.file.dto.FileRequestDTO;
import com.TreadX.file.dto.FileResponseDTO;
import com.TreadX.file.entity.File;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FileMapper {

    public File toEntity(FileRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        return File.builder()
                .filePath(requestDTO.getFilePath())
                .originalFileName(requestDTO.getOriginalFileName())
                .storedFileName(requestDTO.getStoredFileName())
                .fileSize(requestDTO.getFileSize())
                .mimeType(requestDTO.getMimeType())
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    public FileResponseDTO toResponseDTO(File file) {
        if (file == null) {
            return null;
        }
        return FileResponseDTO.builder()
                .id(file.getId())
                .filePath(file.getFilePath())
                .originalFileName(file.getOriginalFileName())
                .storedFileName(file.getStoredFileName())
                .fileSize(file.getFileSize())
                .mimeType(file.getMimeType())
                .uploadedAt(file.getUploadedAt())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}
