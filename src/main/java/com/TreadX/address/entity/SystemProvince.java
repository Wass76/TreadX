package com.TreadX.address.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "system_province")
@Data
//@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemProvince  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String provinceUniqueId;

    private String province;

    @ManyToOne
    @JoinColumn(name = "system_country_id")
    private SystemCountry systemCountry;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private State provinceEntity;
} 