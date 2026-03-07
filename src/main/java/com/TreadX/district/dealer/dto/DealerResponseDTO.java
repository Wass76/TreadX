package com.TreadX.district.dealer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

import com.TreadX.district.dealer.enums.DealerStatus;

@Data
@Builder
public class DealerResponseDTO {
    private Long id;
    private String legalName;
    private String businessName;
    private String email;
    private String phoneNumber;
    private String dealerUniqueId;
    private DealerStatus status;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    
    // User access information
    private Integer totalUsers;
    private Map<String, Integer> userRoles; // e.g., {"DEALER_ADMIN": 2, "DEALER_EMPLOYEE": 5, "DEALER_TECHNICIAN": 3}
} 