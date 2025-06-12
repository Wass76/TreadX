package com.TreadX.lead.repository;

import com.TreadX.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Lead l WHERE l.id = :leadId AND l.createdBy.id = :userId")
    boolean isLeadOwner(@Param("leadId") Long leadId, @Param("userId") Long userId);
} 