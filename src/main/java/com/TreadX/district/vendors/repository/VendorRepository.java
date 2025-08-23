package com.TreadX.district.vendors.repository;

import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.enums.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phone);

    @Query("SELECT v FROM Vendor v WHERE " +
           "LOWER(v.legalName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Vendor> searchVendors(@Param("query") String query, Pageable pageable);

    Page<Vendor> findByVendorStatus(VendorStatus status, Pageable pageable);
    
    Optional<Vendor> findByVendorUniqueId(String vendorUniqueId);
} 