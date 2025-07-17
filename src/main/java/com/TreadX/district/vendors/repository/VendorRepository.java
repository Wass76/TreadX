package com.TreadX.district.vendors.repository;

import com.TreadX.district.vendors.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    @Query("SELECT d FROM Vendor d WHERE " +
           "LOWER(d.legalName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Vendor> searchDealers(@Param("query") String query, Pageable pageable);
} 