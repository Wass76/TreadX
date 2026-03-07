package com.TreadX.plans.repository;

import com.TreadX.plans.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByDealerId(Long dealerId);
    
    Optional<Subscription> findByDealerIdAndStatus(Long dealerId, Subscription.SubscriptionStatus status);
    
    @Query("SELECT s FROM Subscription s WHERE s.dealer.id = :dealerId AND s.status = 'ACTIVE'")
    Optional<Subscription> findActiveSubscriptionByDealerId(@Param("dealerId") Long dealerId);
    
    @Query("SELECT s FROM Subscription s WHERE s.endDate <= :date AND s.status = 'ACTIVE'")
    List<Subscription> findExpiringSubscriptions(@Param("date") LocalDateTime date);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = :status")
    List<Subscription> findByStatus(@Param("status") Subscription.SubscriptionStatus status);
} 