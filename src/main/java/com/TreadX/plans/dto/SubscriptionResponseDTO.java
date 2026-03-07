package com.TreadX.plans.dto;

import com.TreadX.plans.entity.Subscription;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubscriptionResponseDTO {
    private Long id;
    private Long dealerId;
    private String dealerName;
    private Long planId;
    private String planName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Subscription.SubscriptionStatus status;
    private BigDecimal amountPaid;
    private Boolean autoRenew;
    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 