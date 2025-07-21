package com.TreadX.user.mapper;

import com.TreadX.user.dto.TerritoryRequestDTO;
import com.TreadX.user.dto.TerritoryResponseDTO;
import com.TreadX.user.entity.Territory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TerritoryMapper {
    
    /**
     * Convert Territory entity to TerritoryResponseDTO
     */
    public TerritoryResponseDTO toResponseDTO(Territory territory) {
        if (territory == null) {
            return null;
        }
        
        TerritoryResponseDTO dto = new TerritoryResponseDTO();
        dto.setId(territory.getId());
        dto.setCode(territory.getCode());
        dto.setName(territory.getName());
        dto.setLevel(territory.getLevel());
        dto.setParentTerritoryCode(territory.getParentTerritoryCode());
        dto.setDatabaseName(territory.getDatabaseName());
        dto.setIsActive(territory.getIsActive());
        dto.setDescription(territory.getDescription());
        dto.setTimezone(territory.getTimezone());
        dto.setCurrency(territory.getCurrency());
        dto.setCreatedAt(territory.getCreatedAt());
        dto.setUpdatedAt(territory.getUpdatedAt());
        dto.setCreatedBy(territory.getCreatedBy());
        dto.setUpdatedBy(territory.getLastModifiedBy());
        dto.setTerritoryUniqueId(territory.getUniqueId());
        dto.setParentUniqueId(territory.getParentUniqueId());
        
        return dto;
    }
    
    /**
     * Convert Territory entity to TerritoryResponseDTO with hierarchical data
     */
    public TerritoryResponseDTO toResponseDTOWithHierarchy(Territory territory, 
                                                          List<String> childTerritoryCodes,
                                                          List<String> descendantTerritoryCodes,
                                                          List<String> ancestorTerritoryCodes) {
        TerritoryResponseDTO dto = toResponseDTO(territory);
        if (dto != null) {
            dto.setChildTerritoryCodes(childTerritoryCodes);
            dto.setDescendantTerritoryCodes(descendantTerritoryCodes);
            dto.setAncestorTerritoryCodes(ancestorTerritoryCodes);
            dto.setTotalChildTerritories(childTerritoryCodes != null ? childTerritoryCodes.size() : 0);
            dto.setTotalDescendantTerritories(descendantTerritoryCodes != null ? descendantTerritoryCodes.size() : 0);
        }
        return dto;
    }
    
    /**
     * Convert TerritoryRequestDTO to Territory entity
     */
    public Territory toEntity(TerritoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Territory.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .level(dto.getLevel())
                .parentTerritoryCode(dto.getParentTerritoryCode())
                .databaseUrl(dto.getDatabaseUrl())
                .databaseName(dto.getDatabaseName())
                .databaseUsername(dto.getDatabaseUsername())
                .databasePassword(dto.getDatabasePassword())
                .description(dto.getDescription())
                .timezone(dto.getTimezone())
                .currency(dto.getCurrency())
                .uniqueId(null) // Will be set in service if not present
                .parentUniqueId(null) // Will be set in service
                .isActive(true)
                .build();
    }
    
    /**
     * Update Territory entity with data from TerritoryRequestDTO
     */
    public void updateEntityFromDTO(Territory territory, TerritoryRequestDTO dto) {
        if (territory == null || dto == null) {
            return;
        }
        
        territory.setName(dto.getName());
        territory.setLevel(dto.getLevel());
        territory.setParentTerritoryCode(dto.getParentTerritoryCode());
        territory.setDatabaseUrl(dto.getDatabaseUrl());
        territory.setDatabaseName(dto.getDatabaseName());
        territory.setDatabaseUsername(dto.getDatabaseUsername());
        territory.setDatabasePassword(dto.getDatabasePassword());
        territory.setDescription(dto.getDescription());
        territory.setTimezone(dto.getTimezone());
        territory.setCurrency(dto.getCurrency());
    }
    
    /**
     * Convert list of Territory entities to list of TerritoryResponseDTO
     */
    public List<TerritoryResponseDTO> toResponseDTOList(List<Territory> territories) {
        if (territories == null) {
            return null;
        }
        
        return territories.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
} 