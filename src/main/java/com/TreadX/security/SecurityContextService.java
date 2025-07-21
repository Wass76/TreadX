package com.TreadX.security;

import com.TreadX.user.entity.User;
import com.TreadX.user.service.UserService;
import com.TreadX.user.repository.UserTerritoryAccessRepository;
import com.TreadX.user.repository.VendorStaffRepository;
import com.TreadX.user.entity.UserTerritoryAccess;
import com.TreadX.user.entity.VendorStaff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityContextService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserTerritoryAccessRepository userTerritoryAccessRepository;
    @Autowired
    private VendorStaffRepository vendorStaffRepository;

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new SecurityException("User not found: " + email));
    }

    /**
     * Get current user ID
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Get all territories that current user can access
     */
    public List<String> getAccessibleTerritories() {
        User currentUser = getCurrentUser();
        
        // Platform Admin can access all territories
        if (hasRole("PLATFORM_ADMIN")) {
            return getAllTerritoryCodes();
        }
        
        // Get user's territory access
        List<UserTerritoryAccess> territoryAccess = userTerritoryAccessRepository
                .findByUserId(currentUser.getId());
        
        return territoryAccess.stream()
                .map(UserTerritoryAccess::getTerritoryCode)
                .collect(Collectors.toList());
    }

    /**
     * Check if current user can access specific territory
     */
    public boolean canAccessTerritory(String territoryCode) {
        User currentUser = getCurrentUser();
        
        // Platform Admin can access any territory
        if (hasRole("PLATFORM_ADMIN")) {
            return true;
        }
        
        // Check user's territory access
        return userTerritoryAccessRepository
                .existsByUserIdAndTerritoryCode(currentUser.getId(), territoryCode);
    }

    /**
     * Check if current user can access vendor data
     */
    public boolean canAccessVendorData(Long vendorId, String districtCode, String operation) {
        User currentUser = getCurrentUser();
        
        // Platform Admin can access any vendor data
        if (hasRole("PLATFORM_ADMIN")) {
            return true;
        }
        
        // Check if user is vendor staff
        VendorStaff vendorStaff = vendorStaffRepository
                .findByUserIdAndVendorIdAndDistrictCode(currentUser.getId(), vendorId, districtCode)
                .orElse(null);
        
        if (vendorStaff == null) {
            return false;
        }
        
        // Check vendor access level permissions
        return checkVendorAccessLevel(vendorStaff.getAccessLevel().name(), operation);
    }

    /**
     * Get vendor staff info for current user
     */
    public VendorStaff getVendorStaffInfo() {
        User currentUser = getCurrentUser();
        return vendorStaffRepository.findByUserId(currentUser.getId()).orElse(null);
    }

    /**
     * Check if current user has specific role
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
    }

    /**
     * Check if current user is Platform Admin
     */
    public boolean isPlatformAdmin() {
        return hasRole("PLATFORM_ADMIN");
    }

    /**
     * Check if current user is Sales Manager
     */
    public boolean isSalesManager() {
        return hasRole("SALES_MANAGER");
    }

    /**
     * Check if current user is Sales Agent
     */
    public boolean isSalesAgent() {
        return hasRole("SALES_AGENT");
    }

    /**
     * Check if current user is Vendor Staff
     */
    public boolean isVendorStaff() {
        return hasRole("VENDOR_STAFF");
    }

    /**
     * Get primary territory for current user (for single-territory users)
     */
    public String getPrimaryTerritory() {
        List<String> accessibleTerritories = getAccessibleTerritories();
        
        if (accessibleTerritories.isEmpty()) {
            throw new SecurityException("User has no territory access");
        }
        
        // For single territory users, return the only territory
        if (accessibleTerritories.size() == 1) {
            return accessibleTerritories.get(0);
        }
        
        // For multi-territory users, they need to specify territory
        throw new SecurityException("User has access to multiple territories. Please specify territory.");
    }

    /**
     * Check vendor access level permissions
     */
    private boolean checkVendorAccessLevel(String accessLevel, String operation) {
        switch (accessLevel) {
            case "OWNER":
                return true; // Full access
                
            case "MANAGER":
                return List.of("READ", "WRITE_CUSTOMERS", "WRITE_EMPLOYEES", "WRITE_BASIC").contains(operation);
                
            case "MECHANIC":
                return List.of("READ", "WRITE_TIRES", "WRITE_VEHICLES", "WRITE_TRANSACTIONS").contains(operation);
                
            case "CASHIER":
                return List.of("READ", "WRITE_TRANSACTIONS", "READ_CUSTOMERS").contains(operation);
                
            case "VIEWER":
                return "READ".equals(operation);
                
            default:
                return false;
        }
    }

    /**
     * Get all territory codes (for Platform Admin)
     */
    private List<String> getAllTerritoryCodes() {
        // This will be implemented when we have territory management
        // For now, return hardcoded list
        return List.of("N6B", "N5V", "N7A");
    }
} 