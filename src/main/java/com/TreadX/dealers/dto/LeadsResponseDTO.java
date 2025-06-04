package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.dealers.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadsResponseDTO {
    private Long id;
    private String businessName;
    private String businessEmail;
    private String phoneNumber;
    private LeadSource source;
    private LeadStatus status;
    private AddressResponseDTO address;
    private String notes;
    private Long dealerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long addedBy;
    private Long lastModifiedBy;
} 