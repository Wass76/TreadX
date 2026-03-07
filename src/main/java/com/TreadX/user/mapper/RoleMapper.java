package com.TreadX.user.mapper;

import com.TreadX.user.dto.RoleResponseDTO;
import com.TreadX.user.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {


    public RoleResponseDTO toResponse(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .isActive(role.isActive())
                .isSystem(role.isSystem())
                .isSystemGenerated(role.isSystemGenerated())
                .build();
    }
} 