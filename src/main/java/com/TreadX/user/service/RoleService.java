package com.TreadX.user.service;

import com.TreadX.user.entity.Permission;
import com.TreadX.user.entity.Role;
import com.TreadX.user.repository.PermissionRepository;
import com.TreadX.user.repository.RoleRepository;
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
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role with name " + role.getName() + " already exists");
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
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
        role.setActive(roleDetails.isActive());
        
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