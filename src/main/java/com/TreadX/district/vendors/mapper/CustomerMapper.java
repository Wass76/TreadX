package com.TreadX.district.vendors.mapper;

import com.TreadX.district.vendors.dto.CustomerRequestDTO;
import com.TreadX.district.vendors.dto.CustomerResponseDTO;
import com.TreadX.district.vendors.entity.Customer;
import com.TreadX.district.vendors.entity.CustomerPhone;
import com.TreadX.district.vendors.dto.CustomerPhoneResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    
    /**
     * Convert CustomerRequestDTO to Customer entity
     */
    public Customer toEntity(CustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        
        Customer customer = new Customer();
        
        // Basic Information
        customer.setFirstName(requestDTO.getFirstName());
        customer.setLastName(requestDTO.getLastName());
        customer.setEmail(requestDTO.getEmail());
        
        // Address Information
        customer.setStreetNumber(requestDTO.getStreetNumber());
        customer.setStreetName(requestDTO.getStreetName());
        customer.setAptUnitBldg(requestDTO.getAptUnitBldg());
        customer.setPostalCode(requestDTO.getPostalCode());
        
        // Note: vendor, customerUniqueId are set by the service layer, not from the request DTO
        
        return customer;
    }
    
    /**
     * Convert Customer entity to CustomerResponseDTO
     */
    public CustomerResponseDTO toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        
        // Basic Information
        responseDTO.setId(customer.getId());
        responseDTO.setFirstName(customer.getFirstName());
        responseDTO.setLastName(customer.getLastName());
        responseDTO.setEmail(customer.getEmail());
        
        // Address Information
        responseDTO.setStreetNumber(customer.getStreetNumber());
        responseDTO.setStreetName(customer.getStreetName());
        responseDTO.setAptUnitBldg(customer.getAptUnitBldg());
        responseDTO.setPostalCode(customer.getPostalCode());
        
        // Phone Numbers
        if (customer.getPhoneNumbers() != null) {
            List<CustomerPhoneResponseDTO> phoneDTOs = customer.getPhoneNumbers().stream()
                    .map(this::toPhoneResponseDTO)
                    .collect(Collectors.toList());
            responseDTO.setPhoneNumbers(phoneDTOs);
        }
        
        // Vendor Information
        if (customer.getVendor() != null) {
            responseDTO.setVendorId(customer.getVendor().getId());
            responseDTO.setVendorName(customer.getVendor().getBusinessName());
        }
        responseDTO.setCustomerUniqueId(customer.getCustomerUniqueId());
        
        // Audit Information
        responseDTO.setCreatedAt(customer.getCreatedAt());
        responseDTO.setUpdatedAt(customer.getUpdatedAt());
        responseDTO.setCreatedBy(customer.getCreatedBy() != null ? customer.getCreatedBy().toString() : null);
        responseDTO.setUpdatedBy(customer.getLastModifiedBy() != null ? customer.getLastModifiedBy().toString() : null);
        
        return responseDTO;
    }
    
    /**
     * Update existing Customer entity with data from CustomerRequestDTO
     */
    public void updateEntity(Customer customer, CustomerRequestDTO requestDTO) {
        if (customer == null || requestDTO == null) {
            return;
        }
        
        // Basic Information
        customer.setFirstName(requestDTO.getFirstName());
        customer.setLastName(requestDTO.getLastName());
        customer.setEmail(requestDTO.getEmail());
        
        // Address Information
        customer.setStreetNumber(requestDTO.getStreetNumber());
        customer.setStreetName(requestDTO.getStreetName());
        customer.setAptUnitBldg(requestDTO.getAptUnitBldg());
        customer.setPostalCode(requestDTO.getPostalCode());
        

        // Note: vendor, customerUniqueId are managed by the service layer, not updated from the request DTO
    }
    
    /**
     * Convert CustomerPhone entity to CustomerPhoneResponseDTO
     */
    private CustomerPhoneResponseDTO toPhoneResponseDTO(CustomerPhone phone) {
        if (phone == null) {
            return null;
        }
        
        CustomerPhoneResponseDTO phoneDTO = new CustomerPhoneResponseDTO();
        
        phoneDTO.setId(phone.getId());
        phoneDTO.setPhoneNumber(phone.getPhoneNumber());
        phoneDTO.setPhoneType(phone.getPhoneType() != null ? phone.getPhoneType().name() : null);
        phoneDTO.setPhoneStatus(phone.getPhoneStatus() != null ? phone.getPhoneStatus().name() : null);
        phoneDTO.setIsPrimary(phone.getIsPrimary());
        phoneDTO.setExtension(phone.getExtension());
        phoneDTO.setNotes(phone.getNotes());
        
        // Audit Information
        phoneDTO.setCreatedAt(phone.getCreatedAt());
        phoneDTO.setUpdatedAt(phone.getUpdatedAt());
        phoneDTO.setCreatedBy(phone.getCreatedBy() != null ? phone.getCreatedBy().toString() : null);
        phoneDTO.setUpdatedBy(phone.getLastModifiedBy() != null ? phone.getLastModifiedBy().toString() : null);
        
        return phoneDTO;
    }
}
