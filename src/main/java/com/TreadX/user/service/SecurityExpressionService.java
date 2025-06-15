package com.TreadX.user.service;

import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authz")
public class SecurityExpressionService extends BaseSecurityService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeadsRepository leadsRepository;
    @Autowired
    private DealerContactRepository dealerContactRepository;
    @Autowired
    private DealerRepository dealerRepository;

    public SecurityExpressionService(UserRepository userRepository) {
        super(userRepository);
    }

    /**
     * Checks if the current user is a Sales Manager
     */
    public boolean isSalesManager() {
        User currentUser = getCurrentUser();
        return currentUser.getRole().getName().equals("SALES_MANAGER");
    }

    /**
     * Checks if the current user is a Sales Agent
     */
    public boolean isSalesAgent() {
        User currentUser = getCurrentUser();
        return currentUser.getRole().getName().equals("SALES_AGENT");
    }

    /**
     * Checks if the current user is a Platform Admin
     */
    public boolean isPlatformAdmin() {
        User currentUser = getCurrentUser();
        return currentUser.getRole().getName().equals("PLATFORM_ADMIN");
    }

    /**
     * Checks if the current user owns the lead
     */
    public boolean isLeadOwner(Long leadId) {
        User currentUser = getCurrentUser();
        var lead = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
        return lead.getCreatedBy().equals(currentUser.getId());
    }

    /**
     * Checks if the current user owns the contact
     */
    public boolean isContactOwner(Long contactId) {
        User currentUser = getCurrentUser();
        var contact = dealerContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));
        return contact.getOwner().getId().equals(currentUser.getId());
    }

    /**
     * Checks if the current user is the same as the requested user
     * @param userId ID of the user to check against
     * @return true if the current user is the same as the requested user
     */
    public boolean isCurrentUser(Long userId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().equals(userId);
    }

    /**
     * Checks if the current user has a specific permission
     * @param permissionName The permission to check for
     * @return true if the user has the permission
     */
    public boolean hasPermission(String permissionName) {
        User currentUser = getCurrentUser();
        
        // Check role permissions
        boolean hasRolePermission = currentUser.getRole().getPermissions().stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
        
        if (hasRolePermission) {
            return true;
        }
        
        // Check additional permissions
        return currentUser.getAdditionalPermissions().stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }
} 