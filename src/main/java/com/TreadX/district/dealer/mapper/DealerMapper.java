package com.TreadX.district.dealer.mapper;

import com.TreadX.district.dealer.dto.DealerRequestDTO;
import com.TreadX.district.dealer.dto.DealerResponseDTO;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.enums.DealerStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DealerMapper {
    
    private final ObjectMapper objectMapper;
    
    public Dealer toEntity(DealerRequestDTO request) {
        Dealer dealer = new Dealer();
        dealer.setLegalName(request.getLegalName());
        dealer.setBusinessName(request.getBusinessName());
        dealer.setStreetNumber(request.getStreetNumber());
        dealer.setStreetName(request.getStreetName());
        dealer.setAptUnitBldg(request.getAptUnitBldg());
        dealer.setPostalCode(request.getPostalCode());
        dealer.setEmail(request.getEmail());
        dealer.setPhoneNumber(request.getPhoneNumber());
        dealer.setDealerStatus(request.getStatus() != null ? request.getStatus() : DealerStatus.ACTIVE);
        
        // User access management
        dealer.setTotalUsers(request.getTotalUsers());
        dealer.setUserRolesConfig(convertUserRolesToJson(request.getUserRoles()));
        
        return dealer;
    }

    public DealerResponseDTO toResponse(Dealer dealer) {
        return DealerResponseDTO.builder()
                .id(dealer.getId())
                .legalName(dealer.getLegalName())
                .businessName(dealer.getBusinessName())
                .email(dealer.getEmail())
                .phoneNumber(dealer.getPhoneNumber())
                .dealerUniqueId(dealer.getDealerUniqueId())
                .status(dealer.getDealerStatus())
                .streetNumber(dealer.getStreetNumber())
                .streetName(dealer.getStreetName())
                .aptUnitBldg(dealer.getAptUnitBldg())
                .postalCode(dealer.getPostalCode())
                .createdAt(dealer.getCreatedAt())
                .updatedAt(dealer.getUpdatedAt())
                .totalUsers(dealer.getTotalUsers())
                .userRoles(convertJsonToUserRoles(dealer.getUserRolesConfig()))
                .build();
    }

    public void updateEntityFromRequest(Dealer dealer, DealerRequestDTO request) {
        dealer.setLegalName(request.getLegalName());
        dealer.setBusinessName(request.getBusinessName());
        dealer.setStreetNumber(request.getStreetNumber());
        dealer.setStreetName(request.getStreetName());
        dealer.setAptUnitBldg(request.getAptUnitBldg());
        dealer.setPostalCode(request.getPostalCode());
        dealer.setEmail(request.getEmail());
        dealer.setPhoneNumber(request.getPhoneNumber());
        if (request.getStatus() != null) {
            dealer.setDealerStatus(request.getStatus());
        }
    }

    /**
     * Updates vendor entity with only the fields that are present (not null) in the request.
     * This method is useful for partial updates where you don't want to overwrite existing data.
     */
    public void updateEntityFromRequestPartial(Dealer dealer, DealerRequestDTO request) {
        if (request.getLegalName() != null) {
            dealer.setLegalName(request.getLegalName());
        }
        if (request.getBusinessName() != null) {
            dealer.setBusinessName(request.getBusinessName());
        }
        if (request.getStreetNumber() != null) {
            dealer.setStreetNumber(request.getStreetNumber());
        }
        if (request.getStreetName() != null) {
            dealer.setStreetName(request.getStreetName());
        }
        if (request.getAptUnitBldg() != null) {
            dealer.setAptUnitBldg(request.getAptUnitBldg());
        }
        if (request.getPostalCode() != null) {
            dealer.setPostalCode(request.getPostalCode());
        }
        if (request.getEmail() != null) {
            dealer.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            dealer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getStatus() != null) {
            dealer.setDealerStatus(request.getStatus());
        }
        if (request.getTotalUsers() != null) {
            dealer.setTotalUsers(request.getTotalUsers());
        }
        if (request.getUserRoles() != null) {
            dealer.setUserRolesConfig(convertUserRolesToJson(request.getUserRoles()));
        }
    }
    
    private String convertUserRolesToJson(Map<String, Integer> userRoles) {
        try {
            return objectMapper.writeValueAsString(userRoles);
        } catch (JsonProcessingException e) {
            log.error("Error converting user roles to JSON", e);
            return "{}";
        }
    }
    
    private Map<String, Integer> convertJsonToUserRoles(String userRolesJson) {
        try {
            if (userRolesJson == null || userRolesJson.trim().isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(userRolesJson, new TypeReference<Map<String, Integer>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to user roles", e);
            return Map.of();
        }
    }
} 