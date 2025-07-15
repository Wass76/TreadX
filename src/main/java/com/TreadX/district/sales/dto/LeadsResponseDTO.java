package com.TreadX.district.sales.dto;

import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.dealers.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.TreadX.user.dto.UserResponseDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadsResponseDTO {
    private Long id;
    private String businessName;
    private String businessEmail;
    private String phoneNumber;
    // Flattened address fields
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    // New lead source fields
    private LeadSource source;
    private String sourceUrl;
    private String uploadedFile;
    private LeadStatus status;
    private String notes;
    private Long dealerId;
    private String dealerUniqueId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long addedBy;
    private Long lastModifiedBy;
    private com.TreadX.user.dto.UserResponseDTO validatedBy;
    private java.time.LocalDateTime validatedAt;
} 