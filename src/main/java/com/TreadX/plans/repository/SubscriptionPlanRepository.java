package com.TreadX.plans.repository;

import com.TreadX.plans.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    Optional<SubscriptionPlan> findByPlanName(String planName);
    
    Page<SubscriptionPlan> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.isActive = true AND sp.maxUsers >= :userCount")
    List<SubscriptionPlan> findActivePlansByUserCount(@Param("userCount") Integer userCount);
    
    boolean existsByPlanName(String planName);
} 