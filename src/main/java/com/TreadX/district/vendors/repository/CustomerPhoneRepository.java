package com.TreadX.district.vendors.repository;

import com.TreadX.district.vendors.entity.CustomerPhone;
import com.TreadX.district.vendors.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPhoneRepository extends JpaRepository<CustomerPhone, Long> {
    
    // Find all phone numbers for a customer
    List<CustomerPhone> findByCustomer(Customer customer);
    
    // Find all phone numbers for a customer with specific type
    List<CustomerPhone> findByCustomerAndPhoneType(Customer customer, CustomerPhone.PhoneType phoneType);
    
    // Find primary phone number for a customer
    Optional<CustomerPhone> findByCustomerAndIsPrimaryTrue(Customer customer);
    
    // Find phone numbers by status
    List<CustomerPhone> findByCustomerAndPhoneStatus(Customer customer, CustomerPhone.PhoneStatus phoneStatus);
    
    // Check if phone number exists for any customer
    @Query("SELECT COUNT(cp) > 0 FROM CustomerPhone cp WHERE cp.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    // Find phone numbers by phone number (for duplicate checking)
    List<CustomerPhone> findByPhoneNumber(String phoneNumber);
    
    // Delete all phone numbers for a customer
    void deleteByCustomer(Customer customer);
    
    // Find phone numbers by customer ID
    @Query("SELECT cp FROM CustomerPhone cp WHERE cp.customer.id = :customerId")
    List<CustomerPhone> findByCustomerId(@Param("customerId") Long customerId);
}