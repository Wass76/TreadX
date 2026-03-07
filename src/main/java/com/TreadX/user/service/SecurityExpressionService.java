package com.TreadX.user.service;


import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("authz")
public class SecurityExpressionService extends BaseSecurityService {

    
    @Autowired
    private LeadsRepository leadsRepository;


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
     * A user owns a lead if:
     * 1. They created the lead, OR
     * 2. The lead is assigned to them
     */
    public boolean isLeadOwner(Long leadId) {
        User currentUser = getCurrentUser();
        var lead = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
        
        // Check if the user created the lead
        boolean isCreator = lead.getCreatedBy().equals(currentUser.getId());
        
        var current = lead.getCurrentHistory();
        boolean isAssigned = current != null && current.getAssignedTo() != null && current.getAssignedTo().getId().equals(currentUser.getId());
        
        return isCreator || isAssigned;
    }

    /**
     * Checks if the current user can access the lead
     * A user can access a lead if:
     * 1. They are a manager/admin (full access), OR
     * 2. They created the lead, OR
     * 3. The lead is assigned to them, OR
     * 4. They are an agent and the lead is an unassigned manager lead
     */
    public boolean canAccessLead(Long leadId) {
        User currentUser = getCurrentUser();
        var lead = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
        
        // Managers and admins can access all leads
        if (isSalesManager() || isPlatformAdmin()) {
            return true;
        }
        
        // Check if the user created the lead
        boolean isCreator = lead.getCreatedBy().equals(currentUser.getId());
        
        var current = lead.getCurrentHistory();
        boolean isAssigned = current != null && current.getAssignedTo() != null && current.getAssignedTo().getId().equals(currentUser.getId());
        
        if (isSalesAgent()) {
            boolean isUnassignedManagerLead = current != null && Boolean.TRUE.equals(current.getAddedByManager()) && current.getAssignedTo() == null;
            return isCreator || isAssigned || isUnassignedManagerLead;
        }
        
        return isCreator || isAssigned;
    }

    /**
     * Checks if the current user owns the contact
     */
    // public boolean isContactOwner(Long contactId) {
    //     User currentUser = getCurrentUser();
    //     var contact = dealerContactRepository.findById(contactId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));
    //     return contact.getOwner().getId().equals(currentUser.getId());
    // }

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