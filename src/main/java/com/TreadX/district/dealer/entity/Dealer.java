package com.TreadX.district.dealer.entity;

import com.TreadX.district.dealer.enums.DealerStatus;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Dealer")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Dealer extends AuditedEntity {
    private String legalName;
    private String businessName;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private DealerStatus dealerStatus;
    private String dealerUniqueId;
    
    // User access management fields
    @Column(name = "total_users")
    private Integer totalUsers;
    
    @Column(name = "user_roles_config", columnDefinition = "TEXT")
    private String userRolesConfig; // JSON string storing role counts like {"DEALER_ADMIN": 2, "DEALER_EMPLOYEE": 5, "DEALER_TECHNICIAN": 3}

    protected String getSequenceName() {
        return "dealer_id_seq";
    }
} 