package com.TreadX.user.repository;

import com.TreadX.user.entity.UserTerritoryAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTerritoryAccessRepository extends JpaRepository<UserTerritoryAccess, Long> {

    List<UserTerritoryAccess> findByUserId(Long userId);

    @Query("SELECT uta.territoryCode FROM UserTerritoryAccess uta WHERE uta.user.id = :userId")
    List<String> findTerritoryCodesByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndTerritoryCode(Long userId, String territoryCode);

    @Query("SELECT uta.territoryCode FROM UserTerritoryAccess uta WHERE uta.user.id = :userId AND uta.accessLevel = 'ADMIN'")
    List<String> findAdminTerritoryCodesByUserId(@Param("userId") Long userId);
} 