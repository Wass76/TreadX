package com.TreadX.district.vendors.mapper;

import com.TreadX.district.vendors.dto.VendorRequestDTO;
import com.TreadX.district.vendors.dto.VendorResponseDTO;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.enums.VendorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VendorMapper {
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
    }
} 