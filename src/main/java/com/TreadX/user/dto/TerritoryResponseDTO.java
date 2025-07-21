package com.TreadX.user.dto;

import com.TreadX.user.Enum.TerritoryLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerritoryResponseDTO {
    
    private Long id;
    private String code;
    private String name;
    private TerritoryLevel level;
    private String parentTerritoryCode;
    private String databaseName; // Only database name, not full URL or credentials
    private Boolean isActive;
    private String description;
    private String timezone;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    // Unique identifier for the territory (e.g., 001010001)
    private String territoryUniqueId;

    // Unique identifier for the parent territory
    private String parentUniqueId;
    
    // Additional fields for hierarchical data
    private List<String> childTerritoryCodes;
    private List<String> descendantTerritoryCodes;
    private List<String> ancestorTerritoryCodes;
    private Integer totalChildTerritories;
    private Integer totalDescendantTerritories;
} 