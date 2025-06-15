package com.TreadX.dealers.repository;

import com.TreadX.dealers.entity.Dealer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT d FROM Dealer d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.phone) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Dealer> searchDealers(@Param("query") String query, Pageable pageable);
} 