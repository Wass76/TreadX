package com.TreadX.district.vendors.service;

import com.TreadX.district.vendors.dto.CustomerRequestDTO;
import com.TreadX.district.vendors.dto.CustomerResponseDTO;
import com.TreadX.district.vendors.entity.Customer;
import com.TreadX.district.vendors.entity.CustomerPhone;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.mapper.CustomerMapper;
import com.TreadX.district.vendors.repository.CustomerRepository;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.user.service.AuthorizationService;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final CustomerPhoneService customerPhoneService;
    private final VendorRepository vendorRepository;
    private final CustomerMapper customerMapper;
    private final AuthorizationService authorizationService;
    
    /**
     * Create a new customer for a vendor
     */
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        log.info("Creating customer for vendor: {}", requestDTO.getVendorId());
        
        // Validate vendor exists
        Vendor vendor = vendorRepository.findById(requestDTO.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + requestDTO.getVendorId()));
        
        // Check for duplicate customer
        if (existsDuplicateCustomer(requestDTO, vendor.getId())) {
            throw new ConflictException("Customer with same name, address, and phone already exists");
        }
        
        // Create customer entity
        Customer customer = customerMapper.toEntity(requestDTO);
        customer.setVendor(vendor);
        customer.setCustomerUniqueId("CUST" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // Save customer first
        Customer savedCustomer = customerRepository.save(customer);
        
        // Create phone numbers
        if (requestDTO.getPhoneNumbers() != null && !requestDTO.getPhoneNumbers().isEmpty()) {
            List<CustomerPhone> phoneNumbers = customerPhoneService.createPhoneNumbers(savedCustomer, requestDTO.getPhoneNumbers());
            savedCustomer.setPhoneNumbers(phoneNumbers);
        }
        
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return customerMapper.toResponse(savedCustomer);
    }
    
    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        // Load phone numbers
        List<CustomerPhone> phoneNumbers = customerPhoneService.getPhoneNumbersByCustomer(customer);
        customer.setPhoneNumbers(phoneNumbers);
        
        return customerMapper.toResponse(customer);
    }
    
    /**
     * Get customers by vendor ID (paginated)
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getCustomersByVendor(Long vendorId, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByVendorId(vendorId, pageable);
        
        // Load phone numbers for each customer
        customers.getContent().forEach(customer -> {
            List<CustomerPhone> phoneNumbers = customerPhoneService.getPhoneNumbersByCustomer(customer);
            customer.setPhoneNumbers(phoneNumbers);
        });
        
        return customers.map(customerMapper::toResponse);
    }
    
    /**
     * Get customers for current user's vendor
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getMyVendorCustomers(Pageable pageable) {
        Long vendorId = getCurrentUserVendorId();
        return getCustomersByVendor(vendorId, pageable);
    }
    
    /**
     * Update customer
     */
    public CustomerResponseDTO updateCustomer(Long customerId, CustomerRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        // Update customer fields
        customerMapper.updateEntity(customer, requestDTO);
        
        // Update phone numbers
        if (requestDTO.getPhoneNumbers() != null) {
            customerPhoneService.updatePhoneNumbers(customer, requestDTO.getPhoneNumbers());
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        
        // Load phone numbers for response
        List<CustomerPhone> phoneNumbers = customerPhoneService.getPhoneNumbersByCustomer(updatedCustomer);
        updatedCustomer.setPhoneNumbers(phoneNumbers);
        
        return customerMapper.toResponse(updatedCustomer);
    }
    
    /**
     * Delete customer
     */
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        // Delete phone numbers first
        customerPhoneService.deletePhoneNumbersByCustomer(customer);
        
        // Delete customer
        customerRepository.delete(customer);
        log.info("Customer deleted successfully with ID: {}", customerId);
    }
    
    /**
     * Search customers by vendor and search term
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomersByVendor(Long vendorId, String searchTerm, Pageable pageable) {
        Page<Customer> customers = customerRepository.searchByVendorAndTerm(vendorId, searchTerm, pageable);
        
        // Load phone numbers for each customer
        customers.getContent().forEach(customer -> {
            List<CustomerPhone> phoneNumbers = customerPhoneService.getPhoneNumbersByCustomer(customer);
            customer.setPhoneNumbers(phoneNumbers);
        });
        
        return customers.map(customerMapper::toResponse);
    }
    
    /**
     * Check if duplicate customer exists
     */
    private boolean existsDuplicateCustomer(CustomerRequestDTO requestDTO, Long vendorId) {
        return customerRepository.existsDuplicateCustomer(
                requestDTO.getFirstName(),
                requestDTO.getLastName(),
                requestDTO.getStreetNumber(),
                requestDTO.getPostalCode(),
                requestDTO.getPhoneNumbers().get(0).getPhoneNumber() // Check first phone number
        );
    }
    
    /**
     * Get current user's vendor ID
     */
    private Long getCurrentUserVendorId() {
        // TODO: Implement vendor staff association
        throw new UnsupportedOperationException("Vendor ID retrieval for vendor staff not yet implemented");
    }
}