package com.TreadX.dealers.entity;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.enums.DealerStatus;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "DEALER")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Dealer extends AuditedEntity {
    @Column(nullable = false,updatable = false)
    private String name;
    
    @Column(nullable = false, unique = true,updatable = false)
    private String email;
    
    @Column(nullable = false, unique = true,updatable = false)
    private String phone;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DealerStatus status;
    
    private Integer accessCount;
    
    @Column(unique = true,updatable = false)
    private String dealerUniqueId;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL)
    private List<DealerContact> contacts;

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL)
    private List<DealerEmployee> employees;

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL)
    private List<Leads> leads;

    protected String getSequenceName() {
        return "dealer_id_seq";
    }
} 