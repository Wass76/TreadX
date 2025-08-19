package com.TreadX.district.vendors.service;

import com.TreadX.district.vendors.dto.CustomerPhoneRequestDTO;
import com.TreadX.district.vendors.dto.CustomerPhoneResponseDTO;
import com.TreadX.district.vendors.entity.Customer;
import com.TreadX.district.vendors.entity.CustomerPhone;
import com.TreadX.district.vendors.repository.CustomerPhoneRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerPhoneService {
    
    private final CustomerPhoneRepository customerPhoneRepository;
    private final AuthorizationService authorizationService;
    
    /**
     * Create phone numbers for a customer
     */
    public List<CustomerPhone> createPhoneNumbers(Customer customer, List<CustomerPhoneRequestDTO> phoneRequests) {
        if (phoneRequests == null || phoneRequests.isEmpty()) {
            return List.of();
        }
        
        // Clear existing phone numbers if this is an update
        customerPhoneRepository.deleteByCustomer(customer);
        
        List<CustomerPhone> phoneNumbers = phoneRequests.stream()
                .map(request -> createPhoneNumber(customer, request))
                .collect(Collectors.toList());
        
        // Ensure only one primary phone number
        ensureSinglePrimaryPhone(phoneNumbers);
        
        // Save all phone numbers
        return customerPhoneRepository.saveAll(phoneNumbers);
    }
    
    /**
     * Create a single phone number
     */
    public CustomerPhone createPhoneNumber(Customer customer, CustomerPhoneRequestDTO request) {
        CustomerPhone phoneNumber = CustomerPhone.builder()
                .customer(customer)
                .phoneNumber(request.getPhoneNumber())
                .phoneType(request.getPhoneType())
                .phoneStatus(request.getPhoneStatus())
                .isPrimary(request.getIsPrimary())
                .extension(request.getExtension())
                .notes(request.getNotes())
                .build();
        
        return phoneNumber;
    }
    
    /**
     * Update phone numbers for a customer
     */
    public List<CustomerPhone> updatePhoneNumbers(Customer customer, List<CustomerPhoneRequestDTO> phoneRequests) {
        return createPhoneNumbers(customer, phoneRequests);
    }
    
    /**
     * Get all phone numbers for a customer
     */
    public List<CustomerPhone> getPhoneNumbersByCustomer(Customer customer) {
        return customerPhoneRepository.findByCustomer(customer);
    }
    
    /**
     * Get phone numbers by customer ID
     */
    public List<CustomerPhone> getPhoneNumbersByCustomerId(Long customerId) {
        return customerPhoneRepository.findByCustomerId(customerId);
    }
    
    /**
     * Get primary phone number for a customer
     */
    public CustomerPhone getPrimaryPhoneNumber(Customer customer) {
        return customerPhoneRepository.findByCustomerAndIsPrimaryTrue(customer)
                .orElse(null);
    }
    
    /**
     * Get phone numbers by type for a customer
     */
    public List<CustomerPhone> getPhoneNumbersByType(Customer customer, CustomerPhone.PhoneType phoneType) {
        return customerPhoneRepository.findByCustomerAndPhoneType(customer, phoneType);
    }
    
    /**
     * Check if phone number exists for any customer
     */
    public boolean phoneNumberExists(String phoneNumber) {
        return customerPhoneRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Find customers by phone number
     */
    public List<CustomerPhone> findByPhoneNumber(String phoneNumber) {
        return customerPhoneRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Delete phone numbers for a customer
     */
    public void deletePhoneNumbersByCustomer(Customer customer) {
        customerPhoneRepository.deleteByCustomer(customer);
    }
    
    /**
     * Ensure only one primary phone number exists
     */
    private void ensureSinglePrimaryPhone(List<CustomerPhone> phoneNumbers) {
        long primaryCount = phoneNumbers.stream()
                .filter(CustomerPhone::getIsPrimary)
                .count();
        
        if (primaryCount == 0 && !phoneNumbers.isEmpty()) {
            // Set first phone number as primary if none specified
            phoneNumbers.get(0).setIsPrimary(true);
        } else if (primaryCount > 1) {
            // Keep only the first primary, set others to false
            boolean firstPrimaryFound = false;
            for (CustomerPhone phone : phoneNumbers) {
                if (phone.getIsPrimary() && !firstPrimaryFound) {
                    firstPrimaryFound = true;
                } else if (phone.getIsPrimary()) {
                    phone.setIsPrimary(false);
                }
            }
        }
    }
    
    /**
     * Convert CustomerPhone entity to DTO
     */
    public CustomerPhoneResponseDTO toResponseDTO(CustomerPhone phoneNumber) {
        return CustomerPhoneResponseDTO.builder()
                .id(phoneNumber.getId())
                .phoneNumber(phoneNumber.getPhoneNumber())
                .phoneType(phoneNumber.getPhoneType().name())
                .phoneStatus(phoneNumber.getPhoneStatus().name())
                .isPrimary(phoneNumber.getIsPrimary())
                .extension(phoneNumber.getExtension())
                .notes(phoneNumber.getNotes())
                .createdAt(phoneNumber.getCreatedAt())
                .updatedAt(phoneNumber.getUpdatedAt())
                .createdBy(phoneNumber.getCreatedBy() != null ? phoneNumber.getCreatedBy().toString() : null)
                .updatedBy(phoneNumber.getLastModifiedBy() != null ? phoneNumber.getLastModifiedBy().toString() : null)
                .build();
    }
    
    /**
     * Convert list of CustomerPhone entities to DTOs
     */
    public List<CustomerPhoneResponseDTO> toResponseDTOs(List<CustomerPhone> phoneNumbers) {
        return phoneNumbers.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}