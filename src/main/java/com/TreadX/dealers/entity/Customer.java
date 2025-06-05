package com.TreadX.dealers.entity;

import com.TreadX.address.entity.Address;
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

import java.util.List;

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

    @ManyToMany
    @JoinTable(
            name = "CUSTOMER_PHONE_NUMBER",
            joinColumns =@JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "phone_number_id")
    )
    private List<CustomerPhone> customerPhone;

    // Address fields
    @ManyToOne
    private Address address;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;
    private String dealerUniqueId;

    @Column(unique = true)
    private String customerUniqueId;
} 