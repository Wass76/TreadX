package com.TreadX.address.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_city")
@Data
//@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String cityUniqueId;

    private String city;

    @ManyToOne
    private City cityEntity;

    @ManyToOne
    @JoinColumn(name = "system_province_id")
    private SystemProvince systemProvince;

    @ManyToOne
    @JoinColumn(name = "system_country_id")
    private SystemCountry systemCountry;
}