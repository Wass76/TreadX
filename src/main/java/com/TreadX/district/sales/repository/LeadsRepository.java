package com.TreadX.district.sales.repository;

import com.TreadX.district.sales.entity.Leads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadsRepository extends JpaRepository<Leads, Long> {
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.businessName = :businessName AND l.streetNumber = :streetNumber AND l.postalCode = :postalCode AND l.phoneNumber = :phoneNumber")
    boolean existsDuplicateLead(@Param("businessName") String businessName, @Param("streetNumber") String streetNumber, @Param("postalCode") String postalCode, @Param("phoneNumber") String phoneNumber);
}