package com.TreadX.district.vendors.repository;

import com.TreadX.district.vendors.entity.Customer;
import com.TreadX.district.vendors.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByVendor(Vendor vendor);
}