package com.TreadX.district.vendors.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorResponseDTO {
    private Long id;
    private String legalName;
    private String businessName;
    private String email;
    private String phoneNumber;
    private String vendorUniqueId;
    // Add other fields as needed from your flowchart
} 