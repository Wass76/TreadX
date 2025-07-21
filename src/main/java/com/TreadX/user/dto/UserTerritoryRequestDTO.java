package com.TreadX.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTerritoryRequestDTO {
    private Long userId;
    private Long territoryId; // The ID of the territory to assign

    /**
     * Deprecated: Address-based assignment fields (for review/migration only)
     */
    @Deprecated
    private Long baseCountryId;
    @Deprecated
    private Long baseProvinceId;
    @Deprecated
    private Long baseCityId;
    @Deprecated
    private com.TreadX.user.Enum.TerritoryLevel territoryLevel;
} 