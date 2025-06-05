package com.TreadX.address.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ADDRESS")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Address extends AuditedEntity {
    @Column(nullable = false)
    private String streetName;
    private String streetNumber;
    private String unitNumber;
    
    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private SystemCity city;
    
    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private SystemProvince province;
    
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private SystemCountry country;
    
    @Column(name = "postal_code")
    private String postalCode;

    private String specialInstructions;
}
