package com.TreadX.district.vendors.repository;

import com.TreadX.district.vendors.entity.Customer;
import com.TreadX.district.vendors.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByVendor(Vendor vendor);
    
    // Find customers by vendor ID with pagination
    Page<Customer> findByVendorId(Long vendorId, Pageable pageable);
    
    // Check for duplicate customer (name, address, phone combination)
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.firstName = :firstName " +
           "AND c.lastName = :lastName AND c.streetNumber = :streetNumber " +
           "AND c.postalCode = :postalCode " +
           "AND EXISTS (SELECT 1 FROM CustomerPhone cp WHERE cp.customer = c AND cp.phoneNumber = :phoneNumber)")
    boolean existsDuplicateCustomer(@Param("firstName") String firstName,
                                   @Param("lastName") String lastName,
                                   @Param("streetNumber") String streetNumber,
                                   @Param("postalCode") String postalCode,
                                   @Param("phoneNumber") String phoneNumber);
    
    // Search customers by vendor and search term
    @Query("SELECT c FROM Customer c WHERE c.vendor.id = :vendorId " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR EXISTS (SELECT 1 FROM CustomerPhone cp WHERE cp.customer = c AND LOWER(cp.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))))")
    Page<Customer> searchByVendorAndTerm(@Param("vendorId") Long vendorId,
                                         @Param("searchTerm") String searchTerm,
                                         Pageable pageable);
}