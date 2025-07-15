package com.TreadX.dealers.repository;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.entity.Leads;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leads l WHERE l.id = :leadId AND l.createdBy = :userId")
    boolean isLeadOwner(@Param("leadId") Long leadId, @Param("userId") Long userId);
    
    // Geographical filtering methods
    @Query("SELECT l FROM Leads l WHERE l.createdBy = :createdBy AND l.address.city.id IN :cityIds")
    Page<Leads> findByCreatedByAndAddressCityIdIn(@Param("createdBy") Long createdBy, 
                                                  @Param("cityIds") List<Long> cityIds, 
                                                  Pageable pageable);
    
    @Query("SELECT l FROM Leads l WHERE l.address.city.id IN :cityIds")
    Page<Leads> findByAddressCityIdIn(@Param("cityIds") List<Long> cityIds, Pageable pageable);
    
    @Query("SELECT l FROM Leads l WHERE l.dealer.id = :dealerId AND l.createdBy = :createdBy AND l.address.city.id IN :cityIds")
    List<Leads> findByDealerIdAndCreatedByAndAddressCityIdIn(@Param("dealerId") Long dealerId,
                                                             @Param("createdBy") Long createdBy,
                                                             @Param("cityIds") List<Long> cityIds);
    
    @Query("SELECT l FROM Leads l WHERE l.dealer.id = :dealerId AND l.address.city.id IN :cityIds")
    List<Leads> findByDealerIdAndAddressCityIdIn(@Param("dealerId") Long dealerId,
                                                 @Param("cityIds") List<Long> cityIds);
}