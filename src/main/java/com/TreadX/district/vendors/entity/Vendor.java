package com.TreadX.district.vendors.entity;

import com.TreadX.address.entity.Address;
import com.TreadX.district.vendors.enums.VendorStatus;
import com.TreadX.district.sales.entity.DealerContact;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "VENDOR")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Vendor extends AuditedEntity {
    private String legalName;
    private String businessName;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private VendorStatus vendorStatus;
    private String vendorUniqueId;
    
    // User access management fields
    @Column(name = "total_users")
    private Integer totalUsers;
    
    @Column(name = "user_roles_config", columnDefinition = "TEXT")
    private String userRolesConfig; // JSON string storing role counts like {"VENDOR_ADMIN": 2, "VENDOR_EMPLOYEE": 5, "VENDOR_TECHNICIAN": 3}

    protected String getSequenceName() {
        return "dealer_id_seq";
    }
} 