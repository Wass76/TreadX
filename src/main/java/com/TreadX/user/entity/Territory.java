package com.TreadX.user.entity;

import com.TreadX.user.Enum.TerritoryLevel;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "territories")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Territory extends AuditedEntity {
    
    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code; // N6B, N5V, LONDON, ONTARIO, etc.
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // North 6B District, London City, etc.
    
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private TerritoryLevel level; // DISTRICT, CITY, PROVINCE, COUNTRY
    
    @Column(name = "parent_territory_code", length = 10)
    private String parentTerritoryCode; // References another territory code
    
    @Column(name = "database_url", nullable = false)
    private String databaseUrl; // jdbc:postgresql://localhost:5432/treadx_n6b
    
    @Column(name = "database_name", nullable = false, length = 50)
    private String databaseName; // treadx_n6b
    
    @Column(name = "database_username", nullable = false, length = 50)
    private String databaseUsername; // n6b_admin
    
    @Column(name = "database_password", nullable = false)
    private String databasePassword; // encrypted password
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "timezone", length = 50)
    private String timezone; // America/Toronto
    
    @Column(name = "currency", length = 3)
    private String currency; // CAD, USD, etc.
    
    @Column(name = "unique_id", unique = true, length = 16)
    private String uniqueId;
    
    @Column(name = "parent_unique_id", length = 32)
    private String parentUniqueId;
    
    @Override
    protected String getSequenceName() {
        return "territory_id_seq";
    }
} 