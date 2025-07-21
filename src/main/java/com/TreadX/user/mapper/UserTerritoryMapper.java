package com.TreadX.user.mapper;

import com.TreadX.address.dto.CityResponseDTO;
import com.TreadX.address.dto.CountryResponseDTO;
import com.TreadX.address.dto.StateResponseDTO;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import com.TreadX.user.dto.UserTerritoryResponseDTO;
import com.TreadX.user.Enum.TerritoryLevel;
import com.TreadX.user.entity.Territory;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.UserTerritory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTerritoryMapper {

    @Autowired
    private TerritoryMapper territoryMapper;



    public UserTerritory toEntity(Territory territory, boolean isActive, User user) {
        return UserTerritory.builder()
                .user(user)
                .territory(territory)
                .isActive(isActive)
                .build();
    }
    
    public UserTerritoryResponseDTO toResponseDTO(UserTerritory entity) {
        UserTerritoryResponseDTO dto = new UserTerritoryResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setLevel(entity.getTerritory() != null ? entity.getTerritory().getLevel() : null);
        dto.setActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setTerritory(territoryMapper.toResponseDTO(entity.getTerritory()));
        return dto;
    }
} 