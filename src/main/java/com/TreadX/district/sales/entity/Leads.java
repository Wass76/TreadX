package com.TreadX.district.sales.entity;

import com.TreadX.district.vendors.enums.LeadSource;
import com.TreadX.district.vendors.enums.LeadStatus;
import com.TreadX.user.entity.User;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.enums.ContactMethod;

import java.time.LocalDateTime;

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
    private String phoneNumber;

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

    @Enumerated(EnumType.STRING)
    private ContactMethod contactMethod;
    private String contactMethodDetails;

    private String extensionNumber;
    private String contactName;
    private String position;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Vendor vendor;
    private String vendorUniqueId;

    @ManyToOne
    @JoinColumn(name = "validated_by_id")
    private User validatedBy;

    private LocalDateTime validatedAt;

    @Override
    protected String getSequenceName() {
        return "leads_id_seq";
    }
} 