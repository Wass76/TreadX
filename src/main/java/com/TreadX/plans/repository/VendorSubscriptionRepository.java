package com.TreadX.plans.repository;

import com.TreadX.plans.entity.VendorSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorSubscriptionRepository extends JpaRepository<VendorSubscription, Long> {
    
    List<VendorSubscription> findByVendorId(Long vendorId);
    
    Optional<VendorSubscription> findByVendorIdAndStatus(Long vendorId, VendorSubscription.SubscriptionStatus status);
    
    @Query("SELECT vs FROM VendorSubscription vs WHERE vs.vendor.id = :vendorId AND vs.status = 'ACTIVE'")
    Optional<VendorSubscription> findActiveSubscriptionByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT vs FROM VendorSubscription vs WHERE vs.endDate <= :date AND vs.status = 'ACTIVE'")
    List<VendorSubscription> findExpiringSubscriptions(@Param("date") LocalDateTime date);
    
    @Query("SELECT vs FROM VendorSubscription vs WHERE vs.status = :status")
    List<VendorSubscription> findByStatus(@Param("status") VendorSubscription.SubscriptionStatus status);
} 