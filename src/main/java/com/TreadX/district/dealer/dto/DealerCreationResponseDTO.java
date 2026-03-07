package com.TreadX.district.dealer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.TreadX.district.dealer.enums.DealerStatus;

@Data
public class DealerCreationResponseDTO {
    // Basic vendor information
    private Long id;
    private String legalName;
    private String businessName;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private DealerStatus dealerStatus;
    private String dealerUniqueId;
    
    // User access information
    private Integer totalUsers;
    private Map<String, Integer> userRoles;
    private List<UserAccessInfo> userAccessList;
    
    // Subscription information
    private Long subscriptionId;
    private String planName;
    private BigDecimal planPrice;
    private String billingCycle;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private Boolean autoRenew;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class UserAccessInfo {
        private String username;
        private String email;
        private String role;
        private String status; // PENDING, ACTIVE, INACTIVE
    }
} 