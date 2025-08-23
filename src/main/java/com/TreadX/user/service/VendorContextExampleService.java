package com.TreadX.user.service;

import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.user.entity.VendorStaff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Example service demonstrating how to use VendorContextService globally
 * This service shows various ways to access vendor context information
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VendorContextExampleService {

    private final VendorContextService vendorContextService;

    /**
     * Example: Get vendor ID for current user
     */
    public Long getCurrentVendorIdExample() {
        try {
            Long vendorId = vendorContextService.getCurrentVendorId();
            log.info("Current user's vendor ID: {}", vendorId);
            return vendorId;
        } catch (Exception e) {
            log.warn("User is not associated with any vendor: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Example: Get vendor entity for current user
     */
    public Vendor getCurrentVendorExample() {
        try {
            Vendor vendor = vendorContextService.getCurrentVendor();
            log.info("Current user's vendor: {} ({})", vendor.getBusinessName(), vendor.getId());
            return vendor;
        } catch (Exception e) {
            log.warn("Could not retrieve vendor for current user: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Example: Check if user has vendor context
     */
    public boolean checkVendorContextExample() {
        boolean hasContext = vendorContextService.hasVendorContext();
        log.info("Current user has vendor context: {}", hasContext);
        return hasContext;
    }

    /**
     * Example: Get user's access level within vendor
     */
    public String getCurrentUserAccessLevelExample() {
        try {
            VendorStaff.VendorAccessLevel accessLevel = vendorContextService.getCurrentUserAccessLevel();
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
        boolean hasRole = vendorContextService.hasRole(roleName);
        log.info("Current user has role '{}': {}", roleName, hasRole);
        return hasRole;
    }

    /**
     * Example: Get vendor staff record for current user
     */
    public VendorStaff getCurrentVendorStaffExample() {
        try {
            VendorStaff vendorStaff = vendorContextService.getCurrentVendorStaff();
            log.info("Current user's vendor staff record: vendorId={}, accessLevel={}", 
                    vendorStaff.getVendorId(), vendorStaff.getAccessLevel());
            return vendorStaff;
        } catch (Exception e) {
            log.warn("Could not retrieve vendor staff record: {}", e.getMessage());
            return null;
        }
    }
}
