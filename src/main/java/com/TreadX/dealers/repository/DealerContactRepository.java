package com.TreadX.dealers.repository;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.entity.DealerContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerContactRepository extends JpaRepository<DealerContact, Long> {
    List<DealerContact> findByBusinessId(Long dealerId);
    boolean existsByBusinessEmail(String businessEmail);
    boolean existsByBusinessPhone(String businessPhone);
    boolean existsByAddress(Address address);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM DealerContact c WHERE c.id = :contactId AND c.owner.id = :userId")
    boolean isContactOwner(@Param("contactId") Long contactId, @Param("userId") Long userId);
} 