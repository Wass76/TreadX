package com.TreadX.district.sales.dto;

import com.TreadX.district.vendors.enums.LeadSource;
import com.TreadX.district.vendors.enums.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadsRequestDTO {
    @NotBlank(message = "business name is required")
    private String businessName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    // Flattened address fields
    @NotBlank(message = "Street number is required")
    private String streetNumber;
    @NotBlank(message = "Street name is required")
    private String streetName;
    private String aptUnitBldg;
    @NotBlank(message = "Postal code is required")
    private String postalCode;

    // New lead source fields
//    @ValidEnum(enumClass = LeadSource.class)
    private LeadSource source;
    private String sourceUrl;
    private String uploadedFile;

//    @ValidEnum(enumClass = LeadStatus.class)
    private LeadStatus status;
    private String notes;
    private Long dealerId;
}
