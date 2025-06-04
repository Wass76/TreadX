package com.TreadX.address.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = AuditingEntityListener.class)
@Table(name = "CITY")
@EqualsAndHashCode(callSuper = true)
public class City extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "state_code", nullable = false, length = 255)
    public String stateCode;

    @Column(name = "country_code", nullable = false, length = 2)
    public String countryCode;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    public BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    public BigDecimal longitude;

    @Column(name = "flag", nullable = false)
    public Short flag = 1;

    @Column(name = "wikiDataId", length = 255)
    public String wikiDataId;

    @OneToOne
    private SystemCity systemCity;
}
