package com.TreadX.user.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "dealer_staff")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class DealerStaff extends AuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "dealer_id", nullable = false)
    private Long dealerId;

    @Column(name = "district_code", nullable = false)
    private String districtCode;

    @Column(name = "access_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private DealerAccessLevel accessLevel;

    public enum DealerAccessLevel {
        OWNER,      // Full access to dealer data
        MANAGER,    // Manage dealerDealerCustomers, employees, basic operations
        MECHANIC,   // Tire operations, vehicle management
//        CASHIER,    // DealerCustomer transactions, basic dealerDealerCustomer data
        VIEWER      // Read-only access to dealer data
    }

    @Override
    protected String getSequenceName() {
        return "dealer_staff_id_seq";
    }
} 