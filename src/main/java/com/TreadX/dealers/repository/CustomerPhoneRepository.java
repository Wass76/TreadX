package com.TreadX.dealers.repository;

import com.TreadX.dealers.entity.Customer;
import com.TreadX.dealers.entity.CustomerPhone;
import com.TreadX.dealers.enums.PhoneType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerPhoneRepository extends JpaRepository<CustomerPhone, Long> {
    Boolean existsByPhoneNumber(String phoneNumber);
    CustomerPhone findByPhoneNumber(String phoneNumber);
    Optional<CustomerPhone> findByDealerCustomerAndPhoneType(Customer customer, PhoneType phoneType);
    List<CustomerPhone> findAllByDealerCustomer(Customer customer);

    void deleteByDealerCustomer(Customer customer);
}