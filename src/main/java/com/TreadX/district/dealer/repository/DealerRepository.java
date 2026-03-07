package com.TreadX.district.dealer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.enums.DealerStatus;

import java.util.Optional;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phone);

    @Query("SELECT d FROM Dealer d WHERE " +
           "LOWER(v.legalName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Dealer> searchDealers(@Param("query") String query, Pageable pageable);

    Page<Dealer> findByDealerStatus(DealerStatus status, Pageable pageable);
    
    Optional<Dealer> findByDealerUniqueId(String dealerUniqueId);
} 