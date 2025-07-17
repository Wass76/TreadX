package com.TreadX.district.vendors.mapper;

import com.TreadX.district.vendors.dto.VendorRequestDTO;
import com.TreadX.district.vendors.dto.VendorResponseDTO;
import com.TreadX.district.vendors.entity.Vendor;
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
    }
} 