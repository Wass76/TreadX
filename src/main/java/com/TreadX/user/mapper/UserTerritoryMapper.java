package com.TreadX.user.mapper;

import com.TreadX.address.dto.CityResponseDTO;
import com.TreadX.address.dto.CountryResponseDTO;
import com.TreadX.address.dto.StateResponseDTO;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import com.TreadX.user.dto.UserTerritoryResponseDTO;
import com.TreadX.user.entity.TerritoryLevel;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.UserTerritory;
import org.springframework.stereotype.Component;

@Component
public class UserTerritoryMapper {
    
    public UserTerritory toEntity(Long userId, TerritoryLevel level, Long cityId, Long provinceId, Long countryId, boolean isActive, User user) {
        return UserTerritory.builder()
                .user(user)
                .level(level)
                .city(cityId != null ? SystemCity.builder().id(cityId).build() : null)
                .province(provinceId != null ? SystemProvince.builder().id(provinceId).build() : null)
                .country(countryId != null ? SystemCountry.builder().id(countryId).build() : null)
                .isActive(isActive)
                .build();
    }
    
    public UserTerritoryResponseDTO toResponseDTO(UserTerritory entity) {
        UserTerritoryResponseDTO dto = new UserTerritoryResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setLevel(entity.getLevel());
        dto.setActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Map geographical entities
        if (entity.getCity() != null) {
            dto.setCity(CityResponseDTO.builder()
                    .id(entity.getCity().getId())
                    .name(entity.getCity().getCity())
                    .build());
        }
        
        if (entity.getProvince() != null) {
            dto.setProvince(StateResponseDTO.builder()
                    .id(entity.getProvince().getId())
                    .name(entity.getProvince().getProvince())
                    .build());
        }
        
        if (entity.getCountry() != null) {
            dto.setCountry(CountryResponseDTO.builder()
                    .id(entity.getCountry().getId())
                    .name(entity.getCountry().getCountry())
                    .build());
        }
        
        return dto;
    }
} 