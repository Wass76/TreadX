package com.TreadX.dealers.entity;

import com.TreadX.utils.entity.AuditedEntity;
import com.TreadX.dealers.enums.PhoneType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_phone")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPhone extends AuditedEntity {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer dealerCustomer;
    private String customerUniqueId;

    private String phoneNumber;
    private PhoneType phoneType;
    private LocalDateTime addedDate;
    private LocalDateTime updatedDate;
    private String addedBy;
    private String updatedBy;
    private String phoneStatus;
} 