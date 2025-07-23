package com.TreadX.user.util;

import com.TreadX.user.entity.Territory;
import com.TreadX.user.Enum.TerritoryLevel;
import java.util.Comparator;
import java.util.List;

public class TerritoryUtils {
    /**
     * Returns the highest-level (most general) territory from the list.
     * Order: COUNTRY > PROVINCE > CITY > DISTRICT
     */
    public static Territory getPrimaryTerritory(List<Territory> territories) {
        if (territories == null || territories.isEmpty()) return null;
        // Lower ordinal = higher level (COUNTRY=3, ... DISTRICT=0)
        return territories.stream()
                .max(Comparator.comparingInt(t -> t.getLevel().ordinal()))
                .orElse(null);
    }
} 