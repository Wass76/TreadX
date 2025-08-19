package com.TreadX.district.vendors.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "customer")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends AuditedEntity {
    
    // Basic Information
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    // Address Information (Embedded for vendor portal simplicity)
    @Column(name = "street_number")
    private String streetNumber;
    
    @Column(name = "street_name")
    private String streetName;
    
    @Column(name = "apt_unit_bldg")
    private String aptUnitBldg;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    // Phone Numbers - Now managed through separate entity
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CustomerPhone> phoneNumbers;
    
    // Vendor Relationship (only relationship kept)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    
    @Column(name = "customer_unique_id", unique = true)
    private String customerUniqueId; // System-generated unique ID

    @Override
    protected String getSequenceName() {
        return "customer_id_seq";
    }
} 