package com.TreadX.user.service;

import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.repository.DealerRepository;
import com.TreadX.user.entity.DealerStaff;
import com.TreadX.user.repository.DealerStaffRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealerContextService {

    private final DealerStaffRepository dealerStaffRepository;
    private final DealerRepository dealerRepository;

    /**
     * Get the current dealer ID for the authenticated user
     * @return Long dealer ID
     * @throws ResourceNotFoundException if user is not associated with any dealer
     */
    public Long getCurrentDealerId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Getting dealer ID for user: {}", username);
        
        DealerStaff dealerStaff = dealerStaffRepository.findByUserEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found for user: " + username));
        
        log.debug("Found dealer ID: {} for user: {}", dealerStaff.getDealerId(), username);
        return dealerStaff.getDealerId();
    }

    /**
     * Get the current dealer entity for the authenticated user
     * @return Dealer entity
     * @throws ResourceNotFoundException if user is not associated with any dealer
     */
    public Dealer getCurrentDealer() {
        Long dealerId = getCurrentDealerId();
        return dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with ID: " + dealerId));
    }

    /**
     * Get the current dealer staff record for the authenticated user
     * @return DealerStaff entity
     * @throws ResourceNotFoundException if user is not associated with any dealer
     */
    public DealerStaff getCurrentDealerStaff() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Getting dealer staff record for user: {}", username);
        
        DealerStaff dealerStaff = dealerStaffRepository.findByUserEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found for user: " + username));
        
        log.debug("Found dealer staff record for user: {} with dealer ID: {}", username, dealerStaff.getDealerId());
        return dealerStaff;
    }

    /**
     * Check if the current user is associated with a dealer
     * @return boolean true if user has dealer association
     */
    public boolean hasDealerContext() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return dealerStaffRepository.findByUserEmail(username).isPresent();
        } catch (Exception e) {
            log.debug("Error checking dealer context: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the current user's access level within their dealer
     * @return DealerAccessLevel enum value
     * @throws ResourceNotFoundException if user is not associated with any dealer
     */
    public DealerStaff.DealerAccessLevel getCurrentUserAccessLevel() {
        DealerStaff dealerStaff = getCurrentDealerStaff();
        return dealerStaff.getAccessLevel();
    }

    /**
     * Check if the current user has a specific role within their dealer
     * @param roleName the role to check
     * @return boolean true if user has the specified role
     */
    public boolean hasRole(String roleName) {
        try {
            DealerStaff dealerStaff = getCurrentDealerStaff();
            return dealerStaff.getUser().getRole().getName().equals(roleName);
        } catch (Exception e) {
            log.debug("Error checking role: {}", e.getMessage());
            return false;
        }
    }
}
