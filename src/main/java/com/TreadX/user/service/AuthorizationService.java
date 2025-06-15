package com.TreadX.user.service;

import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService extends BaseSecurityService {

    @Autowired
    private LeadsRepository leadsRepository;
    @Autowired
    private DealerContactRepository dealerContactRepository;
    @Autowired
    private DealerRepository dealerRepository;

    public AuthorizationService(
            UserRepository userRepository,
            LeadsRepository leadsRepository,
            DealerContactRepository dealerContactRepository,
            DealerRepository dealerRepository) {
        super(userRepository);
        this.leadsRepository = leadsRepository;
        this.dealerContactRepository = dealerContactRepository;
        this.dealerRepository = dealerRepository;
    }

    /**
     * Checks if the current user has access to a lead
     * @param leadId ID of the lead to check access for
     * @param operation Type of operation (READ, UPDATE, DELETE)
     * @return true if the user has access
     */
    public boolean hasAccessToLead(Long leadId, String operation) {
        User currentUser = getCurrentUser();
        
        // Platform admin has full access
        if (isAdmin()) {
            return true;
        }
        
        // Sales manager has full access
        if (hasRole("SALES_MANAGER")) {
            return true;
        }
        
        // Sales agent can only access their own leads
        if (hasRole("SALES_AGENT")) {
            return leadsRepository.findById(leadId)
                    .map(lead -> lead.getCreatedBy().equals(currentUser.getId()))
                    .orElse(false);
        }
        
        return false;
    }

    /**
     * Checks if the current user has access to a dealer contact
     * @param contactId ID of the contact to check access for
     * @param operation Type of operation (READ, UPDATE, DELETE)
     * @return true if the user has access
     */
    public boolean hasAccessToDealerContact(Long contactId, String operation) {
        User currentUser = getCurrentUser();
        
        // Platform admin has full access
        if (isAdmin()) {
            return true;
        }
        
        // Sales manager has full access
        if (hasRole("SALES_MANAGER")) {
            return true;
        }
        
        // Sales agent can only access contacts they created
        if (hasRole("SALES_AGENT")) {
            return dealerContactRepository.findById(contactId)
                    .map(contact -> contact.getCreatedBy().equals(currentUser.getId()))
                    .orElse(false);
        }
        
        return false;
    }

    /**
     * Checks if the current user has access to a dealer
     * @param dealerId ID of the dealer to check access for
     * @param operation Type of operation (READ, UPDATE, DELETE)
     * @return true if the user has access
     */
    public boolean hasAccessToDealer(Long dealerId, String operation) {
        User currentUser = getCurrentUser();
        
        // Platform admin has full access
        if (isAdmin()) {
            return true;
        }
        
        // Sales manager has full access
        if (hasRole("SALES_MANAGER")) {
            return true;
        }
        
        // Sales agent can only access dealers they created
        if (hasRole("SALES_AGENT")) {
            return dealerRepository.findById(dealerId)
                    .map(dealer -> dealer.getCreatedBy().equals(currentUser.getId()))
                    .orElse(false);
        }
        
        return false;
    }

    /**
     * Checks if the current user has permission to convert leads to contacts
     * @return true if the user has permission
     */
    public boolean hasContactConversionAccess() {
        User currentUser = getCurrentUser();
        return hasRole("SALES_MANAGER") || hasRole("SALES_AGENT");
    }

    /**
     * Checks if the current user has permission to convert leads to dealers
     * @return true if the user has permission
     */
    public boolean hasDealerConversionAccess() {
        User currentUser = getCurrentUser();
        return hasRole("SALES_MANAGER") || hasRole("SALES_AGENT");
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
} 