package com.TreadX.user.service;

import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final LeadsRepository leadsRepository;
    private final DealerContactRepository dealerContactRepository;
    private final DealerRepository dealerRepository;

    /**
     * Checks if the current user has access to a lead
     * @param leadId ID of the lead to check access for
     * @param operation Type of operation (READ, UPDATE, DELETE)
     */
    public void checkLeadAccess(Long leadId, String operation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get the lead
        var lead = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));

        // Sales Manager has full access
        if (currentUser.getRole().getName().equals("SALES_MANAGER")) {
            return;
        }

        // Sales Agent can only access their own leads
        if (currentUser.getRole().getName().equals("SALES_AGENT")) {
            if (!lead.getCreatedBy().equals(currentUser.getId())) {
                throw new AccessDeniedException("You don't have permission to " + operation + " this lead");
            }
        }
    }

    /**
     * Checks if the current user has access to a contact
     * @param contactId ID of the contact to check access for
     * @param operation Type of operation (READ, UPDATE, DELETE)
     */
    public void checkContactAccess(Long contactId, String operation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get the contact
        var contact = dealerContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Sales Manager has full access
        if (currentUser.getRole().getName().equals("SALES_MANAGER")) {
            return;
        }

        // Sales Agent can only access their own contacts
        if (currentUser.getRole().getName().equals("SALES_AGENT")) {
            if (!contact.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You don't have permission to " + operation + " this contact");
            }
        }
    }

    /**
     * Checks if the current user has access to a dealer
     * @param dealerId ID of the dealer to check access for
     * @param operation Type of operation (READ, UPDATE, DELETE)
     */
    public void checkDealerAccess(Long dealerId, String operation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get the dealer
        var dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + dealerId));

        // Platform Admin has full access
        if (currentUser.getRole().getName().equals("PLATFORM_ADMIN")) {
            return;
        }

        // Sales Manager and Sales Agent can only read dealers
        if (currentUser.getRole().getName().equals("SALES_MANAGER") || currentUser.getRole().getName().equals("SALES_AGENT")) {
            if (!operation.equals("READ")) {
                throw new AccessDeniedException("You don't have permission to " + operation + " dealers");
            }
        }
    }

    /**
     * Checks if the current user has permission to convert leads to contacts
     */
    public void checkContactConversionAccess() {
        User currentUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Only Sales Manager and Sales Agent can convert leads to contacts
        if (!currentUser.getRole().getName().equals("SALES_MANAGER") && !currentUser.getRole().getName().equals("SALES_AGENT")) {
            throw new AccessDeniedException("Only Sales Manager and Sales Agent can convert leads to contacts");
        }
    }

    /**
     * Checks if the current user has permission to convert leads to dealers
     */
    public void checkDealerConversionAccess() {
        User currentUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Only Sales Manager and Sales Agent can convert leads to dealers
        if (!currentUser.getRole().getName().equals("SALES_MANAGER") && !currentUser.getRole().getName().equals("SALES_AGENT")) {
            throw new AccessDeniedException("Only Sales Manager and Sales Agent can convert leads to dealers");
        }
    }

    /**
     * Checks if the current user has permission to manage dealers
     */
    public void checkDealerManagementAccess() {
        User currentUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Only Platform Admin can manage dealers
        if (!currentUser.getRole().getName().equals("PLATFORM_ADMIN")) {
            throw new AccessDeniedException("Only Platform Admin can manage dealers");
        }
    }
} 