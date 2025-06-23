package com.TreadX.user.dto;

import com.TreadX.user.entity.TerritoryLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTerritoryRequestDTO {
    private Long userId;
    private TerritoryLevel territoryLevel; // Enum: COUNTRY, PROVINCE, CITY, etc.
    private Long territoryId; // ID of the country, province, or city
    // New fields for base entity assignment (for scalable geographical access control)
    private Long baseCountryId; // Optional: Base Country ID
    private Long baseProvinceId; // Optional: Base Province/State ID
    private Long baseCityId; // Optional: Base City ID
} 