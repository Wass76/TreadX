package com.TreadX.plans.dto;

import com.TreadX.plans.entity.VendorSubscription;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VendorSubscriptionRequestDTO {
    private Long vendorId;
    private Long subscriptionPlanId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal amountPaid;
    private Boolean autoRenew;
} 