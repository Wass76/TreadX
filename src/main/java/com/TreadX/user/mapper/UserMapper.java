package com.TreadX.user.mapper;

import com.TreadX.user.dto.UserRequestDTO;
import com.TreadX.user.dto.UserResponseDTO;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.Role;
import com.TreadX.user.entity.Permission;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.user.repository.PermissionRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public UserMapper(RoleMapper roleMapper, PermissionMapper permissionMapper, 
                     RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
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
                .isActive(user.isActive())
                .isSystem(user.isSystem())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getCreatedBy())
                .build();
    }

    /**
     * Updates user entity with all fields from request (full update)
     */
    public void updateEntityFromRequest(User user, UserRequestDTO request) {
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPosition(request.getPosition());
        user.setActive(request.isActive());
        
        // Update role if roleId is provided
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + request.getRoleId()));
            user.setRole(role);
        }
        
        // Update permissions if permissionIds are provided
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = request.getPermissionIds().stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId)))
                    .collect(Collectors.toSet());
            user.setAdditionalPermissions(permissions);
        }
    }

    /**
     * Updates user entity with only the fields that are present (not null) in the request.
     * This method is useful for partial updates where you don't want to overwrite existing data.
     */
    public void updateEntityFromRequestPartial(User user, UserRequestDTO request) {
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }
        
        // Update role if roleId is provided
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + request.getRoleId()));
            user.setRole(role);
        }
        
        // Update permissions if permissionIds are provided
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = request.getPermissionIds().stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId)))
                    .collect(Collectors.toSet());
            user.setAdditionalPermissions(permissions);
        }
        
        // Note: isActive is a boolean, so we need to handle it differently
        // We'll only update it if it's explicitly set in the request
        // This could be enhanced with a wrapper or optional boolean
    }
} 