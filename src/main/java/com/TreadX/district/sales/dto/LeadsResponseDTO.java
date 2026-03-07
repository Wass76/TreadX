package com.TreadX.district.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.district.dealer.enums.ContactMethod;
import com.TreadX.district.dealer.enums.LeadSource;
import com.TreadX.district.dealer.enums.LeadStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadsResponseDTO {
    private Long id;
    private String businessName;
    private String phoneNumber;
    // Flattened address fields
    private AddressResponseDTO address;
    // Formatted address fields
    private String city;
    private String province;
    private String country;
    private String formattedAddress;
    // New lead source fields
    private LeadSource source;
    private String sourceUrl;
    private String uploadedFile; // URL to download the file: /api/v1/leads/{id}/file
    private String previewUrl; // URL to preview the file: /api/v1/leads/{id}/preview
    private LeadStatus status;
    private String notes;
    private Long dealerId;
    private String dealerUniqueId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long addedBy;
    private String addedByName;
    private Long lastModifiedBy;
    private Long validatedBy;
    private String validatedByFirstName;
    private String validatedByLastName;
    private LocalDateTime validatedAt;
    private ContactMethod contactMethod;
    private String contactMethodDetails;
    private String extensionNumber;
    private String contactName;
    private String position;
    // New fields for flag feature and lead assignment
    private Boolean flag;
    private Boolean addedByManager;
    private Long assignedTo;
    private String assignedToFirstName;
    private String assignedToLastName;
    private LocalDateTime assignedAt;
} 