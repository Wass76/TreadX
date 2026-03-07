package com.TreadX.plans.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubscriptionRequestDTO {
    private Long dealerId;
    private Long planId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal amountPaid;
    private Boolean autoRenew;
} 