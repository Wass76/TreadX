package com.TreadX.user.repository;

import com.TreadX.user.entity.UserTerritory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTerritoryRepository extends JpaRepository<UserTerritory, Long> {
    /**
     * Find all active territory assignments for a user
     */
    List<UserTerritory> findByUser_IdAndIsActiveTrue(Long userId);

    /**
     * Find all active assignments for a given territory
     */
    List<UserTerritory> findByTerritory_IdAndIsActiveTrue(Long territoryId);

    /**
     * Find all active assignments
     */
    List<UserTerritory> findByIsActiveTrue();

    /**
     * Check if a user has access to a specific territory
     */
    boolean existsByUser_IdAndTerritory_IdAndIsActiveTrue(Long userId, Long territoryId);
} 