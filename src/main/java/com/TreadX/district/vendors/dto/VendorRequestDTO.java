package com.TreadX.district.vendors.dto;

import com.TreadX.district.vendors.enums.VendorStatus;
import lombok.Data;

import java.util.Map;

@Data
public class VendorRequestDTO {
    private Long leadId;
    private String legalName;
    private String businessName;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private VendorStatus status;
    
    // User access management
    private Integer totalUsers;
    private Map<String, Integer> userRoles; // e.g., {"VENDOR_ADMIN": 2, "VENDOR_EMPLOYEE": 5, "VENDOR_TECHNICIAN": 3}
} 