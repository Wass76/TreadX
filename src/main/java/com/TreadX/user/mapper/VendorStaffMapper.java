package com.TreadX.user.mapper;

import com.TreadX.user.dto.VendorStaffResponseDTO;
import com.TreadX.user.entity.VendorStaff;
import org.springframework.stereotype.Component;

@Component
public class VendorStaffMapper {

    public VendorStaffResponseDTO toResponseDTO(VendorStaff vendorStaff) {
        VendorStaffResponseDTO dto = new VendorStaffResponseDTO();
        dto.setId(vendorStaff.getId());
        dto.setUserId(vendorStaff.getUser().getId());
        dto.setFirstName(vendorStaff.getUser().getFirstName());
        dto.setLastName(vendorStaff.getUser().getLastName());
        dto.setEmail(vendorStaff.getUser().getEmail());
        dto.setUsername(vendorStaff.getUser().getEmail()); // Email is used as username
        dto.setPosition(vendorStaff.getUser().getPosition());
        dto.setRole(vendorStaff.getUser().getRole().getName());
        dto.setAccessLevel(vendorStaff.getAccessLevel().name());
        dto.setStatus(vendorStaff.getUser().isActive() ? "ACTIVE" : "INACTIVE");
        return dto;
    }
}
