package com.TreadX.address.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(value = AuditingEntityListener.class)
@Table(name = "states", schema = "public")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "states_id_seq", sequenceName = "states_id_seq", allocationSize = 1)
    public Long id;

    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false, foreignKey = @ForeignKey(name = "states_country_id_fkey"))
    public Country country;

    @Column(name = "country_code", nullable = false, length = 2)
    public String countryCode;

    @Column(name = "fips_code", length = 255)
    public String fipsCode;

    @Column(name = "iso2", length = 255)
    public String iso2;

    @Column(name = "type", length = 191)
    public String type;

    @Column(name = "level")
    public Integer level;

    @Column(name = "parent_id")
    public Integer parentId;

    @Column(name = "latitude", precision = 10, scale = 8)
    public BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    public BigDecimal longitude;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @Column(name = "flag", nullable = false)
    public Short flag = 1;

    @Column(name = "wikiDataId", length = 255)
    public String wikiDataId;
}
