package com.TreadX.user.repository;

import com.TreadX.user.entity.VendorStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorStaffRepository extends JpaRepository<VendorStaff, Long> {

    Optional<VendorStaff> findByUserId(Long userId);

    Optional<VendorStaff> findByUserIdAndVendorIdAndDistrictCode(Long userId, Long vendorId, String districtCode);

    List<VendorStaff> findByVendorIdAndDistrictCode(Long vendorId, String districtCode);

    @Query("SELECT vs FROM VendorStaff vs WHERE vs.user.id = :userId AND vs.districtCode = :districtCode")
    List<VendorStaff> findByUserIdAndDistrictCode(@Param("userId") Long userId, @Param("districtCode") String districtCode);

    boolean existsByUserIdAndVendorIdAndDistrictCode(Long userId, Long vendorId, String districtCode);

    // New methods for vendor portal
    Page<VendorStaff> findByVendorId(Long vendorId, Pageable pageable);

    @Query("SELECT vs FROM VendorStaff vs WHERE vs.user.email = :email AND vs.vendorId = :vendorId")
    Optional<VendorStaff> findByUserEmailAndVendorId(@Param("email") String email, @Param("vendorId") Long vendorId);

    boolean existsByUserEmailAndVendorId(String email, Long vendorId);

    @Query("SELECT COUNT(vs) FROM VendorStaff vs WHERE vs.vendorId = :vendorId AND vs.user.role.name = :roleName")
    long countByVendorIdAndRoleName(@Param("vendorId") Long vendorId, @Param("roleName") String roleName);

    long countByVendorId(Long vendorId);

    @Query("SELECT COUNT(vs) FROM VendorStaff vs WHERE vs.vendorId = :vendorId AND vs.user.isActive = :isActive")
    long countByVendorIdAndUserActive(@Param("vendorId") Long vendorId, @Param("isActive") boolean isActive);

    @Query("SELECT vs FROM VendorStaff vs WHERE vs.id = :staffId AND vs.vendorId = :vendorId")
    Optional<VendorStaff> findByIdAndVendorId(@Param("staffId") Long staffId, @Param("vendorId") Long vendorId);

    @Query("SELECT vs FROM VendorStaff vs WHERE vs.user.email = :email")
    Optional<VendorStaff> findByUserEmail(@Param("email") String email);
} 