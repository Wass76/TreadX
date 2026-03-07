package com.TreadX.tire.entity;

import com.TreadX.district.dealer.DealerCustomer.entity.DealerCustomer;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "tire_history")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TireHistory extends AuditedEntity {
    @ManyToOne
    @JoinColumn(name = "tire_id")
    private Tire tire;

    @ManyToOne
    @JoinColumn(name = "dealer_customer_id")
    private DealerCustomer dealerCustomer;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    private Integer quantity;
    private Double totalAmount;
    private String historyType;
    private String status;
    private LocalDateTime historyDate;
    private String paymentMethod;
    private String notes;

    @Override
    protected String getSequenceName() {
        return "tire_history_id_seq";
    }
} 