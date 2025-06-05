package com.TreadX.dealers.entity;

import com.TreadX.dealers.enums.PhoneStatus;
import com.TreadX.dealers.enums.PhoneType;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_phone")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPhone extends AuditedEntity {
    @ManyToMany
    @JoinTable(
            name = "CUSTOMER_PHONE_NUMBER",
            joinColumns = @JoinColumn(name = "phone_number_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> dealerCustomer;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhoneType phoneType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhoneStatus phoneStatus;
} 