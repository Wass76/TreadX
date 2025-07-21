package com.TreadX.user.repository;

import com.TreadX.user.entity.Territory;
import com.TreadX.user.Enum.TerritoryLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerritoryRepository extends JpaRepository<Territory, Long> {
    
    /**
     * Find territory by code
     */
    Optional<Territory> findByCode(String code);
    
    /**
     * Find territory by code and active status
     */
    Optional<Territory> findByCodeAndIsActiveTrue(String code);
    
    /**
     * Check if territory code exists
     */
    boolean existsByCode(String code);
    
    /**
     * Find all active territories
     */
    List<Territory> findByIsActiveTrue();
    
    /**
     * Find territories by level
     */
    List<Territory> findByLevelAndIsActiveTrue(TerritoryLevel level);
    
    /**
     * Find child territories by parent code
     */
    List<Territory> findByParentTerritoryCodeAndIsActiveTrue(String parentTerritoryCode);
    
    /**
     * Find territories by level and parent code
     */
    List<Territory> findByLevelAndParentTerritoryCodeAndIsActiveTrue(TerritoryLevel level, String parentTerritoryCode);
    
    /**
     * Find all territory codes
     */
    @Query("SELECT t.code FROM Territory t WHERE t.isActive = true")
    List<String> findAllActiveTerritoryCodes();
    
    /**
     * Find territory codes by level
     */
    @Query("SELECT t.code FROM Territory t WHERE t.level = :level AND t.isActive = true")
    List<String> findTerritoryCodesByLevel(@Param("level") TerritoryLevel level);
    
    /**
     * Find child territory codes by parent code
     */
    @Query("SELECT t.code FROM Territory t WHERE t.parentTerritoryCode = :parentCode AND t.isActive = true")
    List<String> findChildTerritoryCodes(@Param("parentCode") String parentCode);
    
    /**
     * Find all descendants of a territory (recursive) - Simplified for now
     * Note: Complex recursive queries moved to service layer
     */
    @Query("SELECT t.code FROM Territory t WHERE t.parentTerritoryCode = :rootCode AND t.isActive = true")
    List<String> findDirectChildTerritoryCodes(@Param("rootCode") String rootCode);
    
    /**
     * Find all ancestors of a territory (recursive) - Simplified for now
     * Note: Complex recursive queries moved to service layer
     */
    @Query("SELECT t.code FROM Territory t WHERE t.code = :childCode AND t.isActive = true")
    List<String> findDirectParentTerritoryCode(@Param("childCode") String childCode);
} 