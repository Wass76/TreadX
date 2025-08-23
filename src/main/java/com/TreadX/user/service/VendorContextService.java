package com.TreadX.user.service;

import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.user.entity.VendorStaff;
import com.TreadX.user.repository.VendorStaffRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorContextService {

    private final VendorStaffRepository vendorStaffRepository;
    private final VendorRepository vendorRepository;

    /**
     * Get the current vendor ID for the authenticated user
     * @return Long vendor ID
     * @throws ResourceNotFoundException if user is not associated with any vendor
     */
    public Long getCurrentVendorId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Getting vendor ID for user: {}", username);
        
        VendorStaff vendorStaff = vendorStaffRepository.findByUserEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for user: " + username));
        
        log.debug("Found vendor ID: {} for user: {}", vendorStaff.getVendorId(), username);
        return vendorStaff.getVendorId();
    }

    /**
     * Get the current vendor entity for the authenticated user
     * @return Vendor entity
     * @throws ResourceNotFoundException if user is not associated with any vendor
     */
    public Vendor getCurrentVendor() {
        Long vendorId = getCurrentVendorId();
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));
    }

    /**
     * Get the current vendor staff record for the authenticated user
     * @return VendorStaff entity
     * @throws ResourceNotFoundException if user is not associated with any vendor
     */
    public VendorStaff getCurrentVendorStaff() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Getting vendor staff record for user: {}", username);
        
        VendorStaff vendorStaff = vendorStaffRepository.findByUserEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for user: " + username));
        
        log.debug("Found vendor staff record for user: {} with vendor ID: {}", username, vendorStaff.getVendorId());
        return vendorStaff;
    }

    /**
     * Check if the current user is associated with a vendor
     * @return boolean true if user has vendor association
     */
    public boolean hasVendorContext() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return vendorStaffRepository.findByUserEmail(username).isPresent();
        } catch (Exception e) {
            log.debug("Error checking vendor context: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the current user's access level within their vendor
     * @return VendorAccessLevel enum value
     * @throws ResourceNotFoundException if user is not associated with any vendor
     */
    public VendorStaff.VendorAccessLevel getCurrentUserAccessLevel() {
        VendorStaff vendorStaff = getCurrentVendorStaff();
        return vendorStaff.getAccessLevel();
    }

    /**
     * Check if the current user has a specific role within their vendor
     * @param roleName the role to check
     * @return boolean true if user has the specified role
     */
    public boolean hasRole(String roleName) {
        try {
            VendorStaff vendorStaff = getCurrentVendorStaff();
            return vendorStaff.getUser().getRole().getName().equals(roleName);
        } catch (Exception e) {
            log.debug("Error checking role: {}", e.getMessage());
            return false;
        }
    }
}
