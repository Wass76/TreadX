package com.TreadX.tire.entity;

import com.TreadX.dealers.entity.Customer;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "tire_transaction")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TireTransaction extends AuditedEntity {
    @ManyToOne
    @JoinColumn(name = "tire_id")
    private Tire tire;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    private Integer quantity;
    private Double totalAmount;
    private String transactionType;
    private String status;
    private LocalDateTime transactionDate;
    private String paymentMethod;
    private String notes;

    @Override
    protected String getSequenceName() {
        return "tire_transaction_id_seq";
    }
} 