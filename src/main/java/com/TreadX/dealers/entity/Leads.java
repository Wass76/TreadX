package com.TreadX.dealers.entity;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.dealers.enums.LeadStatus;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @Column(unique = true)
    private String businessEmail;
    @Column(unique = true)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    @Enumerated(EnumType.STRING)
    private LeadSource source;
    @Enumerated(EnumType.STRING)
    private LeadStatus status;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @Override
    protected String getSequenceName() {
        return "leads_id_seq";
    }
} 