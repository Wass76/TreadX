package com.TreadX.address.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "system_country")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String countryUniqueId;

    private String country;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country countryEntity;
} 