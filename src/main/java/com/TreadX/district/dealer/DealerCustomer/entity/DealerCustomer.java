package com.TreadX.district.dealer.DealerCustomer.entity;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "dealer_customer")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DealerCustomer extends AuditedEntity {

    // Basic Information
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Address Information (Embedded for dealer portal simplicity)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressRequestDTO address;

    // dealer Relationship (only relationship kept)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @Column(name = "dealer_customer_unique_id", unique = true)
    private String dealerCustomerUniqueId; // System-generated unique ID

    @Override
    protected String getSequenceName() {
        return "dealer_customer_id_seq";
    }
}
