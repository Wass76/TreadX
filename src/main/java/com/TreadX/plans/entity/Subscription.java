package com.TreadX.plans.entity;

import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Subscription extends AuditedEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    
    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;
    
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew;
    
    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    public enum SubscriptionStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED,
        SUSPENDED,
        PENDING_PAYMENT
    }
    
    protected String getSequenceName() {
        return "subscription_id_seq";
    }
} 