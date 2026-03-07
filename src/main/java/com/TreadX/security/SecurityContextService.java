package com.TreadX.security;

import com.TreadX.user.entity.User;
import com.TreadX.user.service.UserService;
import com.TreadX.user.repository.DealerStaffRepository;
import com.TreadX.user.entity.DealerStaff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityContextService {

    @Autowired
    private UserService userService;
    @Autowired
    private DealerStaffRepository dealerStaffRepository;

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
     * Check if current user can access dealer data
     */
    public boolean canAccessDealerData(Long dealerId, String districtCode, String operation) {
        User currentUser = getCurrentUser();
        
        // Platform Admin can access any dealer data
        if (hasRole("PLATFORM_ADMIN")) {
            return true;
        }
        
        // Check if user is dealer staff
        DealerStaff dealerStaff = dealerStaffRepository
                .findByUserIdAndDealerIdAndDistrictCode(currentUser.getId(), dealerId, districtCode)
                .orElse(null);
        
        if (dealerStaff == null) {
            return false;
        }
        
        // Check dealer access level permissions
        return checkDealerAccessLevel(dealerStaff.getAccessLevel().name(), operation);
    }

    /**
     * Get dealer staff info for current user
     */
    public DealerStaff getDealerStaffInfo() {
        User currentUser = getCurrentUser();
        return dealerStaffRepository.findByUserId(currentUser.getId()).orElse(null);
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
     * Check if current user is Dealer Staff
     */
    public boolean isDealerStaff() {
        return hasRole("DEALER_STAFF");
    }


    /**
     * Check dealer access level permissions
     */
    private boolean checkDealerAccessLevel(String accessLevel, String operation) {
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


} 