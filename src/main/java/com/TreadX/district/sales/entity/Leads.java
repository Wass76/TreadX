package com.TreadX.district.sales.entity;

import com.TreadX.address.entity.Address;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.enums.ContactMethod;
import com.TreadX.district.dealer.enums.LeadSource;
import com.TreadX.district.dealer.enums.LeadStatus;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

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
    private Dealer dealer;
    private String dealerUniqueId;

    // Validation/assignment state lives in LeadsHistory; use getCurrent*() for latest.
    @Column(name = "flag", nullable = false)
    @lombok.Builder.Default
    private Boolean flag = false; // true if name, phone, or address already exists

    /** History of validation and assignment for this lead (current state = first entry). */
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    @lombok.Builder.Default
    private List<LeadsHistory> history = new ArrayList<>();

    /** Current state from latest history entry (validatedBy, assignedTo, etc.). */
    public LeadsHistory getCurrentHistory() {
        return (history != null && !history.isEmpty()) ? history.get(0) : null;
    }

    @Override
    protected String getSequenceName() {
        return "leads_id_seq";
    }
} 