package com.TreadX.address.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DynamicUpdate
@DynamicInsert
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "COUNTRY")
@EqualsAndHashCode(callSuper = true)
public class Country extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false, unique = true, length = 2)
    private String code;

    @Column(name = "iso3", length = 3)
    private String iso3;

    @Column(name = "numeric_code", length = 3)
    private String numericCode;

    @Column(name = "iso2", length = 2)
    private String iso2;

    @Column(name = "phonecode", length = 255)
    private String phonecode;

    @Column(name = "capital", length = 255)
    private String capital;

    @Column(name = "currency", length = 255)
    private String currency;

    @Column(name = "currency_name", length = 255)
    private String currencyName;

    @Column(name = "currency_symbol", length = 255)
    private String currencySymbol;

    @Column(name = "tld", length = 255)
    private String tld;

    @Column(name = "native", length = 255)
    private String nativeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", foreignKey = @ForeignKey(name = "countries_region_id_fkey"))
    private Region region;

    @Column(name = "subregion", length = 255)
    private String subregion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subregion_id", foreignKey = @ForeignKey(name = "countries_subregion_id_fkey"))
    private Subregion subregionEntity;

    @Column(name = "nationality", length = 255)
    private String nationality;

    @Column(name = "timezones", columnDefinition = "text")
    private String timezones;

    @Column(name = "translations", columnDefinition = "text")
    private String translations;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "emoji", length = 191)
    private String emoji;

    @Column(name = "emojiUnified", length = 191)
    private String emojiUnified;

    @Column(name = "flag", nullable = false)
    private Short flag = 1;

    @Column(name = "\"wikiDataId\"", length = 255)
    private String wikiDataId;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Province> provinces;
}
