package com.TreadX.district.dealer.dto;

import lombok.Data;

import java.util.Map;

import com.TreadX.district.dealer.enums.DealerStatus;

@Data
public class DealerRequestDTO {
    private Long leadId;
    private String legalName;
    private String businessName;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private DealerStatus status;
    
    // User access management
    private Integer totalUsers;
    private Map<String, Integer> userRoles; // e.g., {"DEALER_ADMIN": 2, "DEALER_EMPLOYEE": 5, "DEALER_TECHNICIAN": 3}
} 