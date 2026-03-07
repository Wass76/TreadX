package com.TreadX.user.mapper;

import com.TreadX.user.dto.DealerStaffResponseDTO;
import com.TreadX.user.entity.DealerStaff;
import org.springframework.stereotype.Component;

@Component
public class DealerStaffMapper {

    public DealerStaffResponseDTO toResponseDTO(DealerStaff dealerStaff) {
        DealerStaffResponseDTO dto = new DealerStaffResponseDTO();
        dto.setId(dealerStaff.getId());
        dto.setUserId(dealerStaff.getUser().getId());
        dto.setFirstName(dealerStaff.getUser().getFirstName());
        dto.setLastName(dealerStaff.getUser().getLastName());
        dto.setEmail(dealerStaff.getUser().getEmail());
        dto.setUsername(dealerStaff.getUser().getEmail()); // Email is used as username
        dto.setPosition(dealerStaff.getUser().getPosition());
        dto.setRole(dealerStaff.getUser().getRole().getName());
        dto.setAccessLevel(dealerStaff.getAccessLevel().name());
        dto.setStatus(dealerStaff.getUser().isActive() ? "ACTIVE" : "INACTIVE");
        return dto;
    }
}
