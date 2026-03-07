package com.TreadX.district.sales.repository;

import com.TreadX.district.dealer.enums.LeadStatus;
import com.TreadX.district.sales.entity.Leads;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadsRepository extends JpaRepository<Leads, Long> {
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.businessName = :businessName AND l.address.streetNumber = :streetNumber AND l.address.postalCode = :postalCode AND l.phoneNumber = :phoneNumber")
    boolean existsDuplicateLead(@Param("businessName") String businessName, @Param("streetNumber") String streetNumber, @Param("postalCode") String postalCode, @Param("phoneNumber") String phoneNumber);

    Page<Leads> findByStatus(LeadStatus status, Pageable pageable);

    // New methods for flag feature
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.businessName = :businessName")
    boolean existsByBusinessName(@Param("businessName") String businessName);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.streetNumber = :streetNumber AND l.streetName = :streetName AND l.postalCode = :postalCode")
    boolean existsByAddress(@Param("streetNumber") String streetNumber, @Param("streetName") String streetName, @Param("postalCode") String postalCode);

    // Methods for lead assignment (current state from latest LeadsHistory)
    @Query("SELECT DISTINCT l FROM Leads l JOIN l.history h WHERE h.assignedTo.id = :assignedToId AND h.createdAt = (SELECT MAX(h2.createdAt) FROM LeadsHistory h2 WHERE h2.lead.id = l.id)")
    List<Leads> findByAssignedToId(@Param("assignedToId") Long assignedToId);

    @Query("SELECT DISTINCT l FROM Leads l JOIN l.history h WHERE h.assignedTo.id = :assignedToId AND l.status = :status AND h.createdAt = (SELECT MAX(h2.createdAt) FROM LeadsHistory h2 WHERE h2.lead.id = l.id)")
    List<Leads> findByAssignedToIdAndStatus(@Param("assignedToId") Long assignedToId, @Param("status") LeadStatus status);

    @Query("SELECT DISTINCT l FROM Leads l JOIN l.history h WHERE h.addedByManager = true AND h.assignedTo IS NULL AND h.createdAt = (SELECT MAX(h2.createdAt) FROM LeadsHistory h2 WHERE h2.lead.id = l.id)")
    List<Leads> findUnassignedManagerLeads();

    @Query("SELECT DISTINCT l FROM Leads l JOIN l.history h WHERE l.createdBy = :userId AND h.addedByManager = false AND h.createdAt = (SELECT MAX(h2.createdAt) FROM LeadsHistory h2 WHERE h2.lead.id = l.id)")
    List<Leads> findMyLeads(@Param("userId") Long userId);

    @Query("SELECT DISTINCT l FROM Leads l JOIN l.history h WHERE l.createdBy = :userId AND l.status = :status AND h.addedByManager = false AND h.createdAt = (SELECT MAX(h2.createdAt) FROM LeadsHistory h2 WHERE h2.lead.id = l.id)")
    List<Leads> findMyLeadsByStatus(@Param("userId") Long userId, @Param("status") LeadStatus status);

    // Methods for vendor/dealer
    List<Leads> findByDealerId(Long dealerId);

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