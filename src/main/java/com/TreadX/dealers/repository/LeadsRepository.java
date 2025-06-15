package com.TreadX.dealers.repository;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.entity.Leads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadsRepository extends JpaRepository<Leads, Long> {
    List<Leads> findByDealerId(Long dealerId);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByBusinessEmail(String businessEmail);
    Boolean existsByAddress(Address address);
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.id = :leadId AND l.createdBy.id = :userId")
    boolean isLeadOwner(@Param("leadId") Long leadId, @Param("userId") Long userId);

} 