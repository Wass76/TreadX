package com.TreadX.dealers.repository;

import com.TreadX.dealers.entity.Leads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadsRepository extends JpaRepository<Leads, Long> {
    List<Leads> findByDealerId(Long dealerId);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByBusinessEmail(String businessEmail);
} 