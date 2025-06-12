package com.TreadX.user.service;

import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authz")
@RequiredArgsConstructor
public class SecurityExpressionService {

    private final UserRepository userRepository;
    private final LeadsRepository leadsRepository;
    private final DealerContactRepository dealerContactRepository;
    private final DealerRepository dealerRepository;

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
     * Helper method to get current user
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
} 