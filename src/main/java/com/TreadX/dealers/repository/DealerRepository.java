package com.TreadX.dealers.repository;

import com.TreadX.dealers.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
} 