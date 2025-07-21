package com.TreadX.user.repository;

import com.TreadX.user.entity.VendorStaff;
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
} 