package com.TreadX.user.entity;

import com.TreadX.utils.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "vendor_staff")
@Data
@EqualsAndHashCode(callSuper = true)
public class VendorStaff extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "district_code", nullable = false)
    private String districtCode;

    @Column(name = "access_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private VendorAccessLevel accessLevel;

    public enum VendorAccessLevel {
        OWNER,      // Full access to vendor data
        MANAGER,    // Manage customers, employees, basic operations
        MECHANIC,   // Tire operations, vehicle management
        CASHIER,    // Customer transactions, basic customer data
        VIEWER      // Read-only access to vendor data
    }
} 