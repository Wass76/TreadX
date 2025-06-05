package com.TreadX.dealers.repository;

import com.TreadX.dealers.entity.Customer;
import com.TreadX.dealers.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByDealer(Dealer dealer);
}