package com.TreadX.district.vendors.dto;

import com.TreadX.district.vendors.enums.VendorStatus;
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
    private VendorStatus status;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    // Add other fields as needed from your flowchart
} 