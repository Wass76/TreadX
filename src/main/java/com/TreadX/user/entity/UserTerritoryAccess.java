package com.TreadX.user.entity;

import com.TreadX.utils.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_territory_access")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserTerritoryAccess extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "territory_code", nullable = false)
    private String territoryCode;

    @Column(name = "access_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public enum AccessLevel {
        READ,       // Read-only access
        WRITE,      // Read and write access
        ADMIN       // Full administrative access
    }
} 