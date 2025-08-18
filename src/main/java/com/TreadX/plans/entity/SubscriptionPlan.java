package com.TreadX.plans.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubscriptionPlan extends AuditedEntity {
    
    @Column(name = "plan_name", nullable = false, unique = true)
    private String planName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "billing_cycle", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;
    
    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;
    
    @Column(name = "max_tire_storage", nullable = false)
    private Integer maxTireStorage; // Maximum number of tires that can be stored
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // JSON string of features
    
    public enum BillingCycle {
        MONTHLY,
        QUARTERLY,
        YEARLY
    }
    
    protected String getSequenceName() {
        return "subscription_plan_id_seq";
    }
} 