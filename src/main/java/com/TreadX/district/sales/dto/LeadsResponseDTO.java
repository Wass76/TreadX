package com.TreadX.district.sales.dto;

import com.TreadX.district.vendors.enums.LeadSource;
import com.TreadX.district.vendors.enums.LeadStatus;
import com.TreadX.district.vendors.enums.ContactMethod;
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
    private String phoneNumber;
    // Flattened address fields
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    // New lead source fields
    private LeadSource source;
    private String sourceUrl;
    private String uploadedFile; // URL to download the file: /api/v1/leads/{id}/file
    private String previewUrl; // URL to preview the file: /api/v1/leads/{id}/preview
    private LeadStatus status;
    private String notes;
    private Long vendorId;
    private String vendorUniqueId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long addedBy;
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
} 