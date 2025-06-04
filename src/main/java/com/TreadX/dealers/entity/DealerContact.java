package com.TreadX.dealers.entity;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.enums.Channel;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.user.entity.User;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "dealer_contact")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DealerContact extends AuditedEntity {
    private String firstName;
    private String lastName;

    private String businessName;

    @Column(unique = true)
    private String businessEmail;
    @Column(unique = true)
    private String businessPhone;

    @Enumerated(EnumType.STRING)
    private LeadSource source;

    @ManyToOne
    private User owner;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    private String ex;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    private String position;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer business;

    private String notes;
} 