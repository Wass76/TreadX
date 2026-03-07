package com.TreadX.user.service;

import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.user.entity.DealerStaff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Example service demonstrating how to use DealerContextService globally
 * This service shows various ways to access dealer context information
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DealerContextExampleService {

    private final DealerContextService dealerContextService;

    /**
     * Example: Get dealer ID for current user
     */
    public Long getCurrentDealerIdExample() {
        try {
            Long dealerId = dealerContextService.getCurrentDealerId();
            log.info("Current user's dealer ID: {}", dealerId);
            return dealerId;
        } catch (Exception e) {
            log.warn("User is not associated with any dealer: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Example: Get dealer entity for current user
     */
    public Dealer getCurrentDealerExample() {
        try {
            Dealer dealer = dealerContextService.getCurrentDealer();
            log.info("Current user's dealer: {} ({})", dealer.getBusinessName(), dealer.getId());
            return dealer;
        } catch (Exception e) {
            log.warn("Could not retrieve dealer for current user: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Example: Check if user has dealer context
     */
    public boolean checkDealerContextExample() {
        boolean hasContext = dealerContextService.hasDealerContext();
        log.info("Current user has dealer context: {}", hasContext);
        return hasContext;
    }

    /**
     * Example: Get user's access level within dealer
     */
    public String getCurrentUserAccessLevelExample() {
        try {
            DealerStaff.DealerAccessLevel accessLevel = dealerContextService.getCurrentUserAccessLevel();
            log.info("Current user's access level: {}", accessLevel);
            return accessLevel.name();
        } catch (Exception e) {
            log.warn("Could not determine user's access level: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    /**
     * Example: Check if user has specific role
     */
    public boolean checkUserRoleExample(String roleName) {
        boolean hasRole = dealerContextService.hasRole(roleName);
        log.info("Current user has role '{}': {}", roleName, hasRole);
        return hasRole;
    }

    /**
     * Example: Get dealer staff record for current user
     */
    public DealerStaff getCurrentDealerStaffExample() {
        try {
            DealerStaff dealerStaff = dealerContextService.getCurrentDealerStaff();
            log.info("Current user's dealer staff record: dealerId={}, accessLevel={}", 
                    dealerStaff.getDealerId(), dealerStaff.getAccessLevel());
            return dealerStaff;
        } catch (Exception e) {
            log.warn("Could not retrieve dealer staff record: {}", e.getMessage());
            return null;
        }
    }
}
