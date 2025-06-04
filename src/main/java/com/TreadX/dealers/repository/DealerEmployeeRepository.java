package com.TreadX.dealers.repository;

import com.TreadX.dealers.entity.DealerEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealerEmployeeRepository extends JpaRepository<DealerEmployee, Long> {
    Optional<DealerEmployee> findByEmail(String email);
} 