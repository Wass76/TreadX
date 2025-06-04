package com.TreadX.dealers.entity;

import com.TreadX.utils.entity.AuditedEntity;
import com.TreadX.address.entity.City;
import com.TreadX.address.entity.Country;
import com.TreadX.address.entity.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "customer")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends AuditedEntity {
    private String firstName;
    private String lastName;
    private String email;

    @ManyToOne
    @JoinColumn(name = "phoneNumber")
    private CustomerPhone customerPhone;
    private String homePhone;
    private String businessPhone;

    // Address fields
    private String streetNumber;
    private String streetName;
    private String postalCode;
    private String unitNumber;
    private String specialInstructions;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;
    private String dealerUniqueId;

    @Column(unique = true)
    private String customerUniqueId;

    @Column(unique = true)
    private String dealerCustomerUniqueId;
} 