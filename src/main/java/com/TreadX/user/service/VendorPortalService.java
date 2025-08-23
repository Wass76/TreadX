package com.TreadX.user.service;

import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.user.dto.VendorStaffCreateRequestDTO;
import com.TreadX.user.dto.VendorStaffResponseDTO;
import com.TreadX.user.dto.VendorStaffUpdateRequestDTO;
import com.TreadX.user.entity.Role;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.VendorStaff;
import com.TreadX.user.mapper.VendorStaffMapper;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.VendorStaffRepository;
import com.TreadX.user.config.RoleConstants;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.utils.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VendorPortalService {

    private final VendorStaffRepository vendorStaffRepository;
    private final RoleRepository roleRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final VendorStaffMapper vendorStaffMapper;
    private final VendorContextService vendorContextService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Get vendor staff list for the authenticated vendor
     */
    public Page<VendorStaffResponseDTO> getVendorStaff(Pageable pageable) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Getting staff list for vendor: {}", vendorId);
        
        Page<VendorStaff> staffPage = vendorStaffRepository.findByVendorId(vendorId, pageable);
        return staffPage.map(vendorStaffMapper::toResponseDTO);
    }

    /**
     * Create a new staff member for the vendor
     */
    public VendorStaffResponseDTO createStaffMember(VendorStaffCreateRequestDTO request) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Creating staff member for vendor: {}", vendorId);
        
        // Validate role
        Role role = validateAndGetRole(request.getRole());
        
        // Validate staff limit based on userRolesConfig
        validateStaffLimitForVendor(vendorId, request.getRole());
        
        // Check if email already exists
        if (vendorStaffRepository.existsByUserEmailAndVendorId(request.getEmail(), vendorId)) {
            throw new ConflictException("Email already exists for this vendor");
        }
        
        // Create user
        User user = createUserFromRequest(request, role);
        
        // Create vendor staff record
        VendorStaff vendorStaff = createVendorStaffRecord(user, vendorId);
        
        log.info("Staff member created successfully: {}", user.getEmail());
        return vendorStaffMapper.toResponseDTO(vendorStaff);
    }

    /**
     * Update staff member information
     */
    public VendorStaffResponseDTO updateStaffMember(Long staffId, VendorStaffUpdateRequestDTO request) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Updating staff member: {} for vendor: {}", staffId, vendorId);
        
        VendorStaff vendorStaff = getVendorStaffByIdAndVendor(staffId, vendorId);
        User user = vendorStaff.getUser();
        
        // Update user fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if new email already exists
            if (vendorStaffRepository.existsByUserEmailAndVendorId(request.getEmail(), vendorId)) {
                throw new ConflictException("Email already exists for this vendor");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }
        
        // Update role if provided
        if (request.getRole() != null) {
            Role newRole = validateAndGetRole(request.getRole());
            user.setRole(newRole);
            vendorStaff.setAccessLevel(mapRoleToAccessLevel(newRole));
        }
        
        // Save updates
        vendorStaffRepository.save(vendorStaff);
        
        log.info("Staff member updated successfully: {}", user.getEmail());
        return vendorStaffMapper.toResponseDTO(vendorStaff);
    }

    /**
     * Deactivate a staff member
     */
    public void deactivateStaffMember(Long staffId) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Deactivating staff member: {} for vendor: {}", staffId, vendorId);
        
        VendorStaff vendorStaff = getVendorStaffByIdAndVendor(staffId, vendorId);
        User user = vendorStaff.getUser();
        
        // Don't allow deactivating the last VENDOR_ADMIN
        if (RoleConstants.VENDOR_ADMIN.equals(user.getRole().getName())) {
            long adminCount = vendorStaffRepository.countByVendorIdAndRoleName(vendorId, RoleConstants.VENDOR_ADMIN);
            if (adminCount <= 1) {
                throw new ConflictException("Cannot deactivate the last vendor admin");
            }
        }
        
        user.setActive(false);
        vendorStaffRepository.save(vendorStaff);
        
        log.info("Staff member deactivated successfully: {}", user.getEmail());
    }

    /**
     * Delete a staff member permanently
     */
    public void deleteStaffMember(Long staffId) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Deleting staff member: {} for vendor: {}", staffId, vendorId);
        
        VendorStaff vendorStaff = getVendorStaffByIdAndVendor(staffId, vendorId);
        User user = vendorStaff.getUser();
        
        // Don't allow deleting the last VENDOR_ADMIN
        if (RoleConstants.VENDOR_ADMIN.equals(user.getRole().getName())) {
            long adminCount = vendorStaffRepository.countByVendorIdAndRoleName(vendorId, RoleConstants.VENDOR_ADMIN);
            if (adminCount <= 1) {
                throw new ConflictException("Cannot delete the last vendor admin");
            }
        }
        
        // Delete the vendor staff record first
        vendorStaffRepository.delete(vendorStaff);
        
        // Then delete the user
        userRepository.delete(user);
        
        log.info("Staff member deleted successfully: {}", user.getEmail());
    }

    /**
     * Get staff member details
     */
    public VendorStaffResponseDTO getStaffMember(Long staffId) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Getting staff member details: {} for vendor: {}", staffId, vendorId);
        
        VendorStaff vendorStaff = getVendorStaffByIdAndVendor(staffId, vendorId);
        return vendorStaffMapper.toResponseDTO(vendorStaff);
    }

    /**
     * Get available roles for vendor staff
     */
    public List<String> getAvailableRoles() {
        return Arrays.asList(
            RoleConstants.VENDOR_ADMIN,
            RoleConstants.VENDOR_EMPLOYEE,
            RoleConstants.VENDOR_TECHNICIAN
        );
    }

    /**
     * Get vendor dashboard information
     */
    public Object getVendorDashboard() {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Getting dashboard for vendor: {}", vendorId);
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Staff statistics
        long totalStaff = vendorStaffRepository.countByVendorId(vendorId);
        long activeStaff = vendorStaffRepository.countByVendorIdAndUserActive(vendorId, true);
        
        // Role distribution
        Map<String, Long> roleDistribution = new HashMap<>();
        for (String role : getAvailableRoles()) {
            long count = vendorStaffRepository.countByVendorIdAndRoleName(vendorId, role);
            roleDistribution.put(role, count);
        }
        
        dashboard.put("totalStaff", totalStaff);
        dashboard.put("activeStaff", activeStaff);
        dashboard.put("roleDistribution", roleDistribution);
        dashboard.put("vendorId", vendorId);
        
        return dashboard;
    }

    /**
     * Get vendor user roles usage information based on userRolesConfig
     */
    public Map<String, Object> getVendorUserRolesUsage() {
        Long vendorId = vendorContextService.getCurrentVendorId();
        log.info("Getting user roles usage for vendor: {}", vendorId);
        
        Map<String, Object> usage = new HashMap<>();
        
        // Get vendor entity to access userRolesConfig
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));
        
        String userRolesConfig = vendor.getUserRolesConfig();
        
        if (userRolesConfig == null || userRolesConfig.trim().isEmpty()) {
            usage.put("hasUserRolesConfig", false);
            usage.put("message", "No user roles configuration found. All roles are unlimited.");
            return usage;
        }
        
        try {
            // Parse the JSON configuration
            Map<String, Integer> roleLimits = objectMapper.readValue(userRolesConfig, new TypeReference<Map<String, Integer>>() {});
            
            if (roleLimits.isEmpty()) {
                usage.put("hasUserRolesConfig", false);
                usage.put("message", "User roles configuration is empty. All roles are unlimited.");
                return usage;
            }
            
            usage.put("hasUserRolesConfig", true);
            usage.put("roleLimits", roleLimits);
            
            // Get current usage for each role
            Map<String, Object> roleUsage = new HashMap<>();
            for (String roleName : roleLimits.keySet()) {
                long currentCount = vendorStaffRepository.countByVendorIdAndRoleName(vendorId, roleName);
                Integer maxCount = roleLimits.get(roleName);
                
                Map<String, Object> roleInfo = new HashMap<>();
                roleInfo.put("currentCount", currentCount);
                roleInfo.put("maxCount", maxCount);
                roleInfo.put("remainingSlots", Math.max(0, maxCount - currentCount));
                roleInfo.put("canAddMore", currentCount < maxCount);
                roleInfo.put("usagePercentage", maxCount > 0 ? (currentCount * 100.0 / maxCount) : 0);
                
                roleUsage.put(roleName, roleInfo);
            }
            
            usage.put("roleUsage", roleUsage);
            
            // Overall statistics
            long totalCurrentStaff = vendorStaffRepository.countByVendorIdAndUserActive(vendorId, true);
            int totalMaxStaff = roleLimits.values().stream().mapToInt(Integer::intValue).sum();
            usage.put("totalCurrentStaff", totalCurrentStaff);
            usage.put("totalMaxStaff", totalMaxStaff);
            usage.put("totalRemainingSlots", Math.max(0, totalMaxStaff - totalCurrentStaff));
            
        } catch (JsonProcessingException e) {
            log.error("Error parsing userRolesConfig for vendor {}: {}", vendorId, e.getMessage());
            usage.put("hasUserRolesConfig", false);
            usage.put("message", "Invalid user roles configuration. Please contact support.");
        }
        
        return usage;
    }

    // Private helper methods

    private Role validateAndGetRole(String roleName) {
        if (!getAvailableRoles().contains(roleName)) {
            throw new IllegalArgumentException("Invalid role: " + roleName);
        }
        
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
    }

    private User createUserFromRequest(VendorStaffCreateRequestDTO request, Role role) {
        String generatedPassword = generateRandomPassword();
        
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(generatedPassword))
                .role(role)
                .position(request.getPosition())
                .isActive(true)
                .isSystem(false)
                .build();
        
        // For now, we'll save the user directly
        // In a real implementation, you might want to send the password via email
        log.info("Generated password for user {}: {}", request.getEmail(), generatedPassword);
        
        return userRepository.save(user);
    }

    private VendorStaff createVendorStaffRecord(User user, Long vendorId) {
        VendorStaff vendorStaff = VendorStaff.builder()
                .user(user)
                .vendorId(vendorId)
                .districtCode("DEFAULT") // TODO: Get from current context
                .accessLevel(mapRoleToAccessLevel(user.getRole()))
                .build();
        
        return vendorStaffRepository.save(vendorStaff);
    }

    private VendorStaff getVendorStaffByIdAndVendor(Long staffId, Long vendorId) {
        return vendorStaffRepository.findByIdAndVendorId(staffId, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + staffId));
    }

    private VendorStaff.VendorAccessLevel mapRoleToAccessLevel(Role role) {
        switch (role.getName()) {
            case RoleConstants.VENDOR_ADMIN:
                return VendorStaff.VendorAccessLevel.OWNER;
            case RoleConstants.VENDOR_EMPLOYEE:
                return VendorStaff.VendorAccessLevel.MANAGER;
            case RoleConstants.VENDOR_TECHNICIAN:
                return VendorStaff.VendorAccessLevel.MECHANIC;
            default:
                return VendorStaff.VendorAccessLevel.VIEWER;
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Validate that the vendor can add more staff based on their userRolesConfig
     */
    private void validateStaffLimitForVendor(Long vendorId, String roleName) {
        // Get vendor entity to access userRolesConfig
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));
        
        String userRolesConfig = vendor.getUserRolesConfig();
        
        if (userRolesConfig == null || userRolesConfig.trim().isEmpty()) {
            log.warn("Vendor {} has no userRolesConfig set", vendorId);
            return; // Allow creation if no configuration is set
        }
        
        try {
            // Parse the JSON configuration
            Map<String, Integer> roleLimits = objectMapper.readValue(userRolesConfig, new TypeReference<Map<String, Integer>>() {});
            
            if (roleLimits.isEmpty()) {
                log.warn("Vendor {} has empty userRolesConfig", vendorId);
                return; // Allow creation if configuration is empty
            }
            
            // Get the limit for the specific role
            Integer maxRoleCount = roleLimits.get(roleName);
            
            if (maxRoleCount == null || maxRoleCount <= 0) {
                log.warn("Vendor {} has no limit set for role: {}", vendorId, roleName);
                return; // Allow creation if no limit is set for this role
            }
            
            // Count current active staff with this role
            long currentRoleCount = vendorStaffRepository.countByVendorIdAndRoleName(vendorId, roleName);
            
            if (currentRoleCount >= maxRoleCount) {
                throw new ConflictException(
                    String.format("Cannot create staff member with role '%s'. Vendor has reached the maximum allowed limit of %d users for this role. Current count: %d.", 
                        roleName, maxRoleCount, currentRoleCount)
                );
            }
            
            log.info("Vendor {} has {}/{} staff members with role '{}'. Staff creation allowed.", 
                vendorId, currentRoleCount, maxRoleCount, roleName);
                
        } catch (JsonProcessingException e) {
            log.error("Error parsing userRolesConfig for vendor {}: {}", vendorId, e.getMessage());
            throw new ConflictException("Invalid user roles configuration. Please contact support.");
        }
    }
}
