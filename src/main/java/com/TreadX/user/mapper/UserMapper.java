package com.TreadX.user.mapper;

import com.TreadX.user.dto.UserResponseDTO;
import com.TreadX.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public UserMapper(RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    public UserResponseDTO toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .position(user.getPosition())
                .role(roleMapper.toResponse(user.getRole()))
                .additionalPermissions(user.getAdditionalPermissions().stream()
                        .map(permissionMapper::toResponse)
                        .collect(Collectors.toSet()))
                // Base address fields
                .baseCountryId(user.getBaseCountry() != null ? user.getBaseCountry().getId() : null)
                .baseCountryName(user.getBaseCountry() != null ? user.getBaseCountry().getCountry() : null)
                .baseStateId(user.getBaseState() != null ? user.getBaseState().getId() : null)
                .baseStateName(user.getBaseState() != null ? user.getBaseState().getProvince() : null)
                .baseCityId(user.getBaseCity() != null ? user.getBaseCity().getId() : null)
                .baseCityName(user.getBaseCity() != null ? user.getBaseCity().getCity() : null)
                .build();
    }
} 