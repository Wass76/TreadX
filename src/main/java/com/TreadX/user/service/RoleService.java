package com.TreadX.user.service;

import com.TreadX.user.dto.RoleRequestDTO;
import com.TreadX.user.entity.Permission;
import com.TreadX.user.entity.Role;
import com.TreadX.user.repository.PermissionRepository;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Transactional
    public Role createRole(RoleRequestDTO requestDTO) {
        if (roleRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Role with name " + requestDTO.getName() + " already exists");
        }
        Role roleEntity = new Role();
        roleEntity.setName(requestDTO.getName());
        roleEntity.setDescription(requestDTO.getDescription());
        roleEntity.setActive(requestDTO.getIsActive());
        roleEntity.setPermissions(
                requestDTO.getPermissionIds()
                        .stream()
                        .map(id -> permissionRepository.findById(id)
                                .orElseThrow(
                                        () -> new EntityNotFoundException("Permission not found with id: " + id)))
                .collect(Collectors.toSet())
        );
        return roleRepository.save(roleEntity);
    }

    public List<String> getPermissionsByRoleId(Long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role not exist with Id: " + id)
            ).getPermissions().stream().map(permission -> permission.getName()).collect(Collectors.toList());
    }

    @Transactional
    public Role updateRole(Long id, RoleRequestDTO roleDetails) {
        Role role = getRoleById(id);
        
        if (role.isSystem()) {
            throw new IllegalStateException("Cannot modify system role: " + role.getName());
        }
        
        if (!role.getName().equals(roleDetails.getName()) && 
            roleRepository.existsByName(roleDetails.getName())) {
            throw new IllegalArgumentException("Role with name " + roleDetails.getName() + " already exists");
        }
        
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        role.setActive(roleDetails.getIsActive());
        
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        
        if (role.isSystem()) {
            throw new IllegalStateException("Cannot delete system role: " + role.getName());
        }
        
        roleRepository.delete(role);
    }

    @Transactional
    public Role updateRolePermissions(Long roleId, Set<Long> permissionIds) {
        Role role = getRoleById(roleId);
        
        if (role.isSystem()) {
            throw new IllegalStateException("Cannot modify permissions of system role: " + role.getName());
        }
        
        Set<Permission> permissions = permissionIds.stream()
                .map(id -> permissionRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id)))
                .collect(Collectors.toSet());
        
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }
} 