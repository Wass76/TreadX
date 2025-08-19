package com.TreadX.district.vendors.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "customer_phone")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPhone extends AuditedEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type", nullable = false)
    private PhoneType phoneType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "phone_status", nullable = false)
    private PhoneStatus phoneStatus;
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
    
    @Column(name = "extension")
    private String extension;
    
    @Column(name = "notes")
    private String notes;
    
    public enum PhoneType {
        CELL,
        HOME,
        BUSINESS,
        FAX,
        OTHER
    }
    
    public enum PhoneStatus {
        ACTIVE,
        INACTIVE,
        VERIFIED,
        UNVERIFIED
    }
    
    @Override
    protected String getSequenceName() {
        return "customer_phone_id_seq";
    }
} 