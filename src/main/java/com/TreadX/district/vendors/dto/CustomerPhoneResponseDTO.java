package com.TreadX.district.vendors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPhoneResponseDTO {
    
    private Long id;
    
    private String phoneNumber;
    
    private String phoneType;
    
    private String phoneStatus;
    
    private Boolean isPrimary;
    
    private String extension;
    
    private String notes;
    
    // Audit Information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
} 