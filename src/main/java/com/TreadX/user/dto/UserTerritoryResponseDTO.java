package com.TreadX.user.dto;

import com.TreadX.address.dto.CityResponseDTO;
import com.TreadX.address.dto.CountryResponseDTO;
import com.TreadX.address.dto.StateResponseDTO;
import com.TreadX.user.entity.TerritoryLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTerritoryResponseDTO {
    
    private Long id;
    private Long userId;
    private TerritoryLevel level;
    private CityResponseDTO city;
    private StateResponseDTO province;
    private CountryResponseDTO country;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 