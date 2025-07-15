package com.TreadX.district.sales.entity;

import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.dealers.enums.LeadStatus;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.TreadX.dealers.entity.Dealer;

@Entity
@Table(name = "leads")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
public class Leads extends AuditedEntity {
    private String businessName;
    private String businessEmail;
    private String phoneNumber;

    // Flattened address fields
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;

    // New lead source fields
    @Enumerated(EnumType.STRING)
    private LeadSource source; // GOVERNMENT OR ADS
    private String sourceUrl; // For GOV_URL or Ads
    private String uploadedFile; // File path or reference for uploaded photo/file

    @Enumerated(EnumType.STRING)
    private LeadStatus status;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "validated_by_id")
    private com.TreadX.user.entity.User validatedBy;

    private java.time.LocalDateTime validatedAt;

    @Override
    protected String getSequenceName() {
        return "leads_id_seq";
    }
} 