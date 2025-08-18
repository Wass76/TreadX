package com.TreadX.plans.dto;

import com.TreadX.plans.entity.SubscriptionPlan;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubscriptionPlanResponseDTO {
    private Long id;
    private String planName;
    private String description;
    private BigDecimal price;
    private SubscriptionPlan.BillingCycle billingCycle;
    private Integer maxUsers;
    private Integer maxTireStorage; // Maximum number of tires that can be stored
    private Boolean isActive;
    private List<String> features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 