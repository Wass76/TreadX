package com.TreadX.plans.dto;

import com.TreadX.plans.entity.Plan;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlanRequestDTO {
    private String planName;
    private String description;
        private BigDecimal price;
    private Plan.BillingCycle billingCycle;
    private Integer maxUsers;
    private Integer maxTireStorage; // Maximum number of tires that can be stored
    private Boolean isActive;
    private List<String> features;
} 