package com.TreadX.district.sales.repository;

import com.TreadX.district.sales.entity.LeadsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadsHistoryRepository extends JpaRepository<LeadsHistory, Long> {

    List<LeadsHistory> findByLeadIdOrderByCreatedAtDesc(Long leadId);
}
