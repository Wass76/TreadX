package com.TreadX.district.vendors.mapper;

import com.TreadX.district.vendors.dto.VendorRequestDTO;
import com.TreadX.district.vendors.dto.VendorResponseDTO;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.enums.VendorStatus;
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
public class VendorMapper {
    
    private final ObjectMapper objectMapper;
    
    public Vendor toEntity(VendorRequestDTO request) {
        Vendor vendor = new Vendor();
        vendor.setLegalName(request.getLegalName());
        vendor.setBusinessName(request.getBusinessName());
        vendor.setStreetNumber(request.getStreetNumber());
        vendor.setStreetName(request.getStreetName());
        vendor.setAptUnitBldg(request.getAptUnitBldg());
        vendor.setPostalCode(request.getPostalCode());
        vendor.setEmail(request.getEmail());
        vendor.setPhoneNumber(request.getPhoneNumber());
        vendor.setVendorStatus(request.getStatus() != null ? request.getStatus() : VendorStatus.ACTIVE);
        
        // User access management
        vendor.setTotalUsers(request.getTotalUsers());
        vendor.setUserRolesConfig(convertUserRolesToJson(request.getUserRoles()));
        
        return vendor;
    }

    public VendorResponseDTO toResponse(Vendor vendor) {
        return VendorResponseDTO.builder()
                .id(vendor.getId())
                .legalName(vendor.getLegalName())
                .businessName(vendor.getBusinessName())
                .email(vendor.getEmail())
                .phoneNumber(vendor.getPhoneNumber())
                .vendorUniqueId(vendor.getVendorUniqueId())
                .status(vendor.getVendorStatus())
                .streetNumber(vendor.getStreetNumber())
                .streetName(vendor.getStreetName())
                .aptUnitBldg(vendor.getAptUnitBldg())
                .postalCode(vendor.getPostalCode())
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .totalUsers(vendor.getTotalUsers())
                .userRoles(convertJsonToUserRoles(vendor.getUserRolesConfig()))
                .build();
    }

    public void updateEntityFromRequest(Vendor vendor, VendorRequestDTO request) {
        vendor.setLegalName(request.getLegalName());
        vendor.setBusinessName(request.getBusinessName());
        vendor.setStreetNumber(request.getStreetNumber());
        vendor.setStreetName(request.getStreetName());
        vendor.setAptUnitBldg(request.getAptUnitBldg());
        vendor.setPostalCode(request.getPostalCode());
        vendor.setEmail(request.getEmail());
        vendor.setPhoneNumber(request.getPhoneNumber());
        if (request.getStatus() != null) {
            vendor.setVendorStatus(request.getStatus());
        }
    }

    /**
     * Updates vendor entity with only the fields that are present (not null) in the request.
     * This method is useful for partial updates where you don't want to overwrite existing data.
     */
    public void updateEntityFromRequestPartial(Vendor vendor, VendorRequestDTO request) {
        if (request.getLegalName() != null) {
            vendor.setLegalName(request.getLegalName());
        }
        if (request.getBusinessName() != null) {
            vendor.setBusinessName(request.getBusinessName());
        }
        if (request.getStreetNumber() != null) {
            vendor.setStreetNumber(request.getStreetNumber());
        }
        if (request.getStreetName() != null) {
            vendor.setStreetName(request.getStreetName());
        }
        if (request.getAptUnitBldg() != null) {
            vendor.setAptUnitBldg(request.getAptUnitBldg());
        }
        if (request.getPostalCode() != null) {
            vendor.setPostalCode(request.getPostalCode());
        }
        if (request.getEmail() != null) {
            vendor.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            vendor.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getStatus() != null) {
            vendor.setVendorStatus(request.getStatus());
        }
        if (request.getTotalUsers() != null) {
            vendor.setTotalUsers(request.getTotalUsers());
        }
        if (request.getUserRoles() != null) {
            vendor.setUserRolesConfig(convertUserRolesToJson(request.getUserRoles()));
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