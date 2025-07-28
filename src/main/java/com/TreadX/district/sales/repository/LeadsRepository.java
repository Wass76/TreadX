package com.TreadX.district.sales.repository;

import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.vendors.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadsRepository extends JpaRepository<Leads, Long> {
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.businessName = :businessName AND l.streetNumber = :streetNumber AND l.postalCode = :postalCode AND l.phoneNumber = :phoneNumber")
    boolean existsDuplicateLead(@Param("businessName") String businessName, @Param("streetNumber") String streetNumber, @Param("postalCode") String postalCode, @Param("phoneNumber") String phoneNumber);

    Page<Leads> findByStatus(LeadStatus status, Pageable pageable);

    // New methods for flag feature
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.businessName = :businessName")
    boolean existsByBusinessName(@Param("businessName") String businessName);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.streetNumber = :streetNumber AND l.streetName = :streetName AND l.postalCode = :postalCode")
    boolean existsByAddress(@Param("streetNumber") String streetNumber, @Param("streetName") String streetName, @Param("postalCode") String postalCode);

    // Methods for lead assignment
    List<Leads> findByAssignedToId(Long assignedToId);
    
    List<Leads> findByAssignedToIdAndStatus(Long assignedToId, LeadStatus status);
    
    @Query("SELECT l FROM Leads l WHERE l.addedByManager = true AND l.assignedTo IS NULL")
    List<Leads> findUnassignedManagerLeads();
    
    @Query("SELECT l FROM Leads l WHERE l.addedByManager = false AND l.createdBy = :userId")
    List<Leads> findMyLeads(@Param("userId") Long userId);
    
    @Query("SELECT l FROM Leads l WHERE l.addedByManager = false AND l.createdBy = :userId AND l.status = :status")
    List<Leads> findMyLeadsByStatus(@Param("userId") Long userId, @Param("status") LeadStatus status);

    // Methods for vendor/dealer
    List<Leads> findByVendorId(Long vendorId);

    // Methods for territory (placeholder implementations)
//    @Query("SELECT l FROM Leads l WHERE l.id = :id") // Placeholder - replace with actual territory logic
//    List<Leads> findByTerritoryId(Long territoryId);
    
//    @Query("SELECT l FROM Leads l WHERE l.id = :id") // Placeholder - replace with actual territory logic
//    Page<Leads> findByTerritoryId(Long territoryId, Pageable pageable);
    
//    @Query("SELECT l FROM Leads l WHERE l.id = :id") // Placeholder - replace with actual territory logic
//    List<Leads> findByTerritoryIdAndStatus(Long territoryId, LeadStatus status);
    
//    @Query("SELECT l FROM Leads l WHERE l.id = :id") // Placeholder - replace with actual territory logic
//    Optional<Leads> findByTerritoryIdAndId(Long territoryId, Long id);
}