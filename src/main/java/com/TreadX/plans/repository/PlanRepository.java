package com.TreadX.plans.repository;

import com.TreadX.plans.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    Optional<Plan> findByPlanName(String planName);
    
    Page<Plan> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM Plan p WHERE p.isActive = true AND p.maxUsers >= :userCount")
    List<Plan> findActivePlansByUserCount(@Param("userCount") Integer userCount);
    
    boolean existsByPlanName(String planName);
} 