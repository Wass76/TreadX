package com.TreadX.dealers.repository;

import com.TreadX.dealers.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}