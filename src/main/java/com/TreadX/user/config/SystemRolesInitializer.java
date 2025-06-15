package com.TreadX.user.config;

import com.TreadX.user.entity.Permission;
import com.TreadX.user.entity.Role;
import com.TreadX.user.repository.PermissionRepository;
import com.TreadX.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Order(1) // Run after database migration
public class SystemRolesInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SystemRolesInitializer.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing system roles and permissions...");
        
        // Create permissions
        Map<String, Permission> permissions = createPermissions();
        
        // Create system roles with their permissions
        createSystemRoles(permissions);
        
        log.info("System roles and permissions initialized successfully");
    }

    private Map<String, Permission> createPermissions() {
        Map<String, Permission> permissions = new HashMap<>();
        
        // Define all system permissions
        List<Permission> permissionList = Arrays.asList(
            // User management permissions
            createPermission("USER_CREATE", "Create users", "USER", "CREATE"),
            createPermission("USER_READ", "View users", "USER", "READ"),
            createPermission("USER_UPDATE", "Update users", "USER", "UPDATE"),
            createPermission("USER_DELETE", "Delete users", "USER", "DELETE"),
            
            // Lead management permissions
            createPermission("LEAD_CREATE", "Create leads", "LEAD", "CREATE"),
            createPermission("LEAD_READ", "View leads", "LEAD", "READ"),
            createPermission("LEAD_UPDATE", "Update leads", "LEAD", "UPDATE"),
            createPermission("LEAD_DELETE", "Delete leads", "LEAD", "DELETE"),
            
            // Contact management permissions
            createPermission("CONTACT_CREATE", "Create contacts", "CONTACT", "CREATE"),
            createPermission("CONTACT_READ", "View contacts", "CONTACT", "READ"),
            createPermission("CONTACT_UPDATE", "Update contacts", "CONTACT", "UPDATE"),
            createPermission("CONTACT_DELETE", "Delete contacts", "CONTACT", "DELETE"),
            
            // Dealer management permissions
            createPermission("DEALER_CREATE", "Create dealers", "DEALER", "CREATE"),
            createPermission("DEALER_READ", "View dealers", "DEALER", "READ"),
            createPermission("DEALER_UPDATE", "Update dealers", "DEALER", "UPDATE"),
            createPermission("DEALER_DELETE", "Delete dealers", "DEALER", "DELETE")
        );
        
        // Save permissions and store in map
        for (Permission permission : permissionList) {
            Permission savedPermission = permissionRepository.findByName(permission.getName())
                .orElseGet(() -> permissionRepository.save(permission));
            permissions.put(permission.getName(), savedPermission);
        }
        
        return permissions;
    }

    private void createSystemRoles(Map<String, Permission> permissions) {
        // Platform Admin role with all permissions
        createSystemRole("PLATFORM_ADMIN", "Platform Administrator", 
            new HashSet<>(permissions.values()));
        
        // Sales Manager role with specific permissions
        createSystemRole("SALES_MANAGER", "Sales Manager",
            new HashSet<>(Arrays.asList(
                permissions.get("USER_CREATE"), // Can create sales agents
                permissions.get("USER_READ"),
                permissions.get("LEAD_CREATE"),
                permissions.get("LEAD_READ"),
                permissions.get("LEAD_UPDATE"),
                permissions.get("CONTACT_CREATE"),
                permissions.get("CONTACT_READ"),
                permissions.get("CONTACT_UPDATE"),
                permissions.get("DEALER_READ")
            )));
        
        // Sales Agent role with specific permissions
        createSystemRole("SALES_AGENT", "Sales Agent",
            new HashSet<>(Arrays.asList(
                permissions.get("LEAD_CREATE"),
                permissions.get("LEAD_READ"),
                permissions.get("LEAD_UPDATE"),
                permissions.get("CONTACT_CREATE"),
                permissions.get("CONTACT_READ"),
                permissions.get("CONTACT_UPDATE"),
                permissions.get("DEALER_READ")
            )));
        
        // Support Agent role with specific permissions
        createSystemRole("SUPPORT_AGENT", "Support Agent",
            new HashSet<>(Arrays.asList(
                permissions.get("LEAD_READ"),
                permissions.get("CONTACT_READ"),
                permissions.get("DEALER_READ")
            )));
    }

    private Permission createPermission(String name, String description, String resource, String action) {
        return Permission.builder()
            .name(name)
            .description(description)
            .resource(resource)
            .action(action)
            .isActive(true)
            .isSystemGenerated(true)
            .createdBy(1L) // Set to system user ID
            .build();
    }

    private void createSystemRole(String name, String description, Set<Permission> permissions) {
        Role role = roleRepository.findByName(name)
            .orElseGet(() -> {
                Role newRole = Role.builder()
                    .name(name)
                    .description(description)
                    .isActive(true)
                    .isSystem(true)
                    .createdBy(1L)
                    .build();
                return roleRepository.save(newRole);
            });
        
        role.setPermissions(permissions);
        roleRepository.save(role);
    }
} 