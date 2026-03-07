package com.TreadX.user.repository;

import com.TreadX.user.entity.DealerStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealerStaffRepository extends JpaRepository<DealerStaff, Long> {

    Optional<DealerStaff> findByUserId(Long userId);

    Optional<DealerStaff> findByUserIdAndDealerIdAndDistrictCode(Long userId, Long dealerId, String districtCode);

    List<DealerStaff> findByDealerIdAndDistrictCode(Long dealerId, String districtCode);

    @Query("SELECT ds FROM DealerStaff ds WHERE ds.user.id = :userId AND ds.districtCode = :districtCode")
    List<DealerStaff> findByUserIdAndDistrictCode(@Param("userId") Long userId, @Param("districtCode") String districtCode);

    boolean existsByUserIdAndDealerIdAndDistrictCode(Long userId, Long dealerId, String districtCode);

    // New methods for dealer portal
    Page<DealerStaff> findByDealerId(Long dealerId, Pageable pageable);

    @Query("SELECT ds FROM DealerStaff ds WHERE ds.user.email = :email AND ds.dealerId = :dealerId")
    Optional<DealerStaff> findByUserEmailAndDealerId(@Param("email") String email, @Param("dealerId") Long dealerId);

    boolean existsByUserEmailAndDealerId(String email, Long dealerId);

    @Query("SELECT COUNT(ds) FROM DealerStaff ds WHERE ds.dealerId = :dealerId AND ds.user.role.name = :roleName")
    long countByDealerIdAndRoleName(@Param("dealerId") Long dealerId, @Param("roleName") String roleName);

    long countByDealerId(Long dealerId);

    @Query("SELECT COUNT(ds) FROM DealerStaff ds WHERE ds.dealerId = :dealerId AND ds.user.isActive = :isActive")
    long countByDealerIdAndUserActive(@Param("dealerId") Long dealerId, @Param("isActive") boolean isActive);

    @Query("SELECT ds FROM DealerStaff ds WHERE ds.id = :staffId AND ds.dealerId = :dealerId")
    Optional<DealerStaff> findByIdAndDealerId(@Param("staffId") Long staffId, @Param("dealerId") Long dealerId);

    @Query("SELECT ds FROM DealerStaff ds WHERE ds.user.email = :email")
    Optional<DealerStaff> findByUserEmail(@Param("email") String email);
} 