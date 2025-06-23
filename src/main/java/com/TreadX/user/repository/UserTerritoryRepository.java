package com.TreadX.user.repository;

import com.TreadX.user.entity.TerritoryLevel;
import com.TreadX.user.entity.UserTerritory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTerritoryRepository extends JpaRepository<UserTerritory, Long> {
    
    /**
     * Find all active territories for a user
     */
    List<UserTerritory> findByUser_IdAndIsActiveTrue(Long userId);
    
    /**
     * Find territories by user and level
     */
    List<UserTerritory> findByUser_IdAndLevelAndIsActiveTrue(Long userId, TerritoryLevel level);
    
    /**
     * Find all active territories
     */
    List<UserTerritory> findByIsActiveTrue();
    
    /**
     * Find all city IDs that a user has access to
     */
    @Query("SELECT DISTINCT ut.city.id FROM UserTerritory ut " +
           "WHERE ut.user.id = :userId AND ut.isActive = true " +
           "AND ut.level = 'CITY' AND ut.city.id IS NOT NULL")
    List<Long> findAccessibleCityIds(@Param("userId") Long userId);
    
    /**
     * Find all province IDs that a user has access to
     */
    @Query("SELECT DISTINCT ut.province.id FROM UserTerritory ut " +
           "WHERE ut.user.id = :userId AND ut.isActive = true " +
           "AND ut.level = 'PROVINCE' AND ut.province.id IS NOT NULL")
    List<Long> findAccessibleProvinceIds(@Param("userId") Long userId);
    
    /**
     * Find all country IDs that a user has access to
     */
    @Query("SELECT DISTINCT ut.country.id FROM UserTerritory ut " +
           "WHERE ut.user.id = :userId AND ut.isActive = true " +
           "AND ut.level = 'COUNTRY' AND ut.country.id IS NOT NULL")
    List<Long> findAccessibleCountryIds(@Param("userId") Long userId);
    
    /**
     * Check if user has access to a specific city
     */
    @Query("SELECT COUNT(ut) > 0 FROM UserTerritory ut " +
           "WHERE ut.user.id = :userId AND ut.isActive = true " +
           "AND ((ut.level = 'CITY' AND ut.city.id = :cityId) " +
           "OR (ut.level = 'PROVINCE' AND ut.province.id = :provinceId) " +
           "OR (ut.level = 'COUNTRY' AND ut.country.id = :countryId))")
    boolean hasAccessToLocation(@Param("userId") Long userId, 
                               @Param("cityId") Long cityId,
                               @Param("provinceId") Long provinceId,
                               @Param("countryId") Long countryId);
} 