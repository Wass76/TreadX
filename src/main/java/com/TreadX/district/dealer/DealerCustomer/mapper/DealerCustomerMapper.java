package com.TreadX.district.dealer.DealerCustomer.mapper;

import com.TreadX.address.mapper.AddressMapper;
import com.TreadX.district.dealer.DealerCustomer.dto.DealerCustomerRequestDTO;
import com.TreadX.district.dealer.DealerCustomer.dto.DealerCustomerResponseDTO;
import com.TreadX.district.dealer.DealerCustomer.entity.DealerCustomer;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DealerCustomerMapper {

    private final AddressMapper addressMapper;
    
    /**
     * Convert DealerCustomerRequestDTO to DealerCustomer entity
     */
    public DealerCustomer toEntity(DealerCustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        
        DealerCustomer dealerCustomer = new DealerCustomer();
        
        // Basic Information
        dealerCustomer.setFirstName(requestDTO.getFirstName());
        dealerCustomer.setLastName(requestDTO.getLastName());
        dealerCustomer.setEmail(requestDTO.getEmail());
        
        // Address Information
        dealerCustomer.setAddress(requestDTO.getAddress());
        
        // Note: vendor, dealerDealerCustomerUniqueId are set by the service layer, not from the request DTO
        
        return dealerCustomer;
    }
    
    /**
     * Convert DealerCustomer entity to DealerCustomerResponseDTO
     */
    public DealerCustomerResponseDTO toResponse(DealerCustomer dealerCustomer) {
        if (dealerCustomer == null) {
            return null;
        }
        
        DealerCustomerResponseDTO responseDTO = new DealerCustomerResponseDTO();
        
        // Basic Information
        responseDTO.setId(dealerCustomer.getId());
        responseDTO.setFirstName(dealerCustomer.getFirstName());
        responseDTO.setLastName(dealerCustomer.getLastName());
        responseDTO.setEmail(dealerCustomer.getEmail());
        
        // Address Information
        responseDTO.setAddress(addressMapper.toResponseDTO(dealerCustomer.getAddress()));
        
        // Phone Numbers
        responseDTO.setPhoneNumber(dealerCustomer.getPhoneNumber());
        
        // Vendor Information
        if (dealerCustomer.getDealer() != null) {
            responseDTO.setDealerId(dealerCustomer.getDealer().getId());
            responseDTO.setDealerName(dealerCustomer.getDealer().getBusinessName());
        }
        responseDTO.setDealerCustomerUniqueId(dealerCustomer.getDealerCustomerUniqueId());
        
        // Audit Information
        responseDTO.setCreatedAt(dealerCustomer.getCreatedAt());
        responseDTO.setUpdatedAt(dealerCustomer.getUpdatedAt());
        responseDTO.setCreatedBy(dealerCustomer.getCreatedBy() != null ? dealerCustomer.getCreatedBy().toString() : null);
        responseDTO.setUpdatedBy(dealerCustomer.getLastModifiedBy() != null ? dealerCustomer.getLastModifiedBy().toString() : null);
        
        return responseDTO;
    }
    
    /**
     * Update existing DealerCustomer entity with data from DealerCustomerRequestDTO
     */
    public void updateEntity(DealerCustomer dealerCustomer, DealerCustomerRequestDTO requestDTO) {
        if (dealerCustomer == null || requestDTO == null) {
            return;
        }
        
        // Basic Information
        dealerCustomer.setFirstName(requestDTO.getFirstName());
        dealerCustomer.setLastName(requestDTO.getLastName());
        dealerCustomer.setEmail(requestDTO.getEmail());
        
        // Address Information
        dealerCustomer.setAddress(requestDTO.getAddress());
        dealerCustomer.setPhoneNumber(requestDTO.getPhoneNumber());
        
        // Note: dealer, dealerCustomerUniqueId are managed by the service layer, not updated from the request DTO
    }
}