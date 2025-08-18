package com.TreadX.plans.dto;

import com.TreadX.plans.entity.VendorSubscription;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VendorSubscriptionResponseDTO {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private Long subscriptionPlanId;
    private String planName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private VendorSubscription.SubscriptionStatus status;
    private BigDecimal amountPaid;
    private Boolean autoRenew;
    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 