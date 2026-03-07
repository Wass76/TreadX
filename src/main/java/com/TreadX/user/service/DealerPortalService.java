package com.TreadX.user.service;

import com.TreadX.user.dto.DealerStaffCreateRequestDTO;
import com.TreadX.user.dto.DealerStaffResponseDTO;
import com.TreadX.user.dto.DealerStaffUpdateRequestDTO;
import com.TreadX.user.entity.Role;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.DealerStaff;
import com.TreadX.user.mapper.DealerStaffMapper;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.DealerStaffRepository;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.repository.DealerRepository;
import com.TreadX.user.config.RoleConstants;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.utils.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealerPortalService {

    private final DealerStaffRepository dealerStaffRepository;
    private final RoleRepository roleRepository;
    private final DealerRepository dealerRepository;
    private final PasswordEncoder passwordEncoder;
    private final DealerStaffMapper dealerStaffMapper;
    private final DealerContextService dealerContextService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Get dealer staff list for the authenticated dealer
     */
    public Page<DealerStaffResponseDTO> getDealerStaff(Pageable pageable) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Getting staff list for dealer: {}", dealerId);
        
        Page<DealerStaff> staffPage = dealerStaffRepository.findByDealerId(dealerId, pageable);
        return staffPage.map(dealerStaffMapper::toResponseDTO);
    }

    /**
     * Create a new staff member for the dealer
     */
    public DealerStaffResponseDTO createStaffMember(DealerStaffCreateRequestDTO request) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Creating staff member for dealer: {}", dealerId);
        
        // Validate role
        Role role = validateAndGetRole(request.getRole());
        
        // Validate staff limit based on userRolesConfig
        validateStaffLimitForDealer(dealerId, request.getRole());
        
        // Check if email already exists
        if (dealerStaffRepository.existsByUserEmailAndDealerId(request.getEmail(), dealerId)) {
            throw new ConflictException("Email already exists for this dealer");
        }
        
        // Create user
        User user = createUserFromRequest(request, role);
        
        // Create dealer staff record
        DealerStaff dealerStaff = createDealerStaffRecord(user, dealerId);
        
        log.info("Staff member created successfully: {}", user.getEmail());
        return dealerStaffMapper.toResponseDTO(dealerStaff);
    }

    /**
     * Update staff member information
     */
    public DealerStaffResponseDTO updateStaffMember(Long staffId, DealerStaffUpdateRequestDTO request) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Updating staff member: {} for dealer: {}", staffId, dealerId);
        
        DealerStaff dealerStaff = getDealerStaffByIdAndDealer(staffId, dealerId);
        User user = dealerStaff.getUser();
        
        // Update user fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if new email already exists
            if (dealerStaffRepository.existsByUserEmailAndDealerId(request.getEmail(), dealerId)) {
                throw new ConflictException("Email already exists for this dealer");
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
            dealerStaff.setAccessLevel(mapRoleToAccessLevel(newRole));
        }
        
        // Save updates
        dealerStaffRepository.save(dealerStaff);
        
        log.info("Staff member updated successfully: {}", user.getEmail());
        return dealerStaffMapper.toResponseDTO(dealerStaff);
    }

    /**
     * Deactivate a staff member
     */
    public void deactivateStaffMember(Long staffId) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Deactivating staff member: {} for dealer: {}", staffId, dealerId);
        
        DealerStaff dealerStaff = getDealerStaffByIdAndDealer(staffId, dealerId);
        User user = dealerStaff.getUser();
        
        // Don't allow deactivating the last VENDOR_ADMIN
        if (RoleConstants.DEALER_ADMIN.equals(user.getRole().getName())) {
            long adminCount = dealerStaffRepository.countByDealerIdAndRoleName(dealerId, RoleConstants.DEALER_ADMIN);
            if (adminCount <= 1) {
                throw new ConflictException("Cannot deactivate the last dealer admin");
            }
        }
        
        user.setActive(false);
        dealerStaffRepository.save(dealerStaff);
        
        log.info("Staff member deactivated successfully: {}", user.getEmail());
    }

    /**
     * Delete a staff member permanently
     */
    public void deleteStaffMember(Long staffId) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Deleting staff member: {} for dealer: {}", staffId, dealerId);
        
        DealerStaff dealerStaff = getDealerStaffByIdAndDealer(staffId, dealerId);
        User user = dealerStaff.getUser();
        
        // Don't allow deleting the last VENDOR_ADMIN
        if (RoleConstants.DEALER_ADMIN.equals(user.getRole().getName())) {
            long adminCount = dealerStaffRepository.countByDealerIdAndRoleName(dealerId, RoleConstants.DEALER_ADMIN);
            if (adminCount <= 1) {
                throw new ConflictException("Cannot delete the last dealer admin");
            }
        }
        
        // Delete the dealer staff record first
        dealerStaffRepository.delete(dealerStaff);
        
        // Then delete the user
        userRepository.delete(user);
        
        log.info("Staff member deleted successfully: {}", user.getEmail());
    }

    /**
     * Get staff member details
     */
    public DealerStaffResponseDTO getStaffMember(Long staffId) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Getting staff member details: {} for dealer: {}", staffId, dealerId);
        
        DealerStaff dealerStaff = getDealerStaffByIdAndDealer(staffId, dealerId);
        return dealerStaffMapper.toResponseDTO(dealerStaff);
    }

    /**
     * Get available roles for dealer staff
     */
    public List<String> getAvailableRoles() {
        return Arrays.asList(
            RoleConstants.DEALER_ADMIN,
            RoleConstants.DEALER_EMPLOYEE,
            RoleConstants.DEALER_TECHNICIAN
        );
    }

    /**
     * Get dealer dashboard information
     */
    public Object getDealerDashboard() {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Getting dashboard for dealer: {}", dealerId);
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Staff statistics
        long totalStaff = dealerStaffRepository.countByDealerId(dealerId);
        long activeStaff = dealerStaffRepository.countByDealerIdAndUserActive(dealerId, true);
        
        // Role distribution
        Map<String, Long> roleDistribution = new HashMap<>();
        for (String role : getAvailableRoles()) {
            long count = dealerStaffRepository.countByDealerIdAndRoleName(dealerId, role);
            roleDistribution.put(role, count);
        }
        
        dashboard.put("totalStaff", totalStaff);
        dashboard.put("activeStaff", activeStaff);
        dashboard.put("roleDistribution", roleDistribution);
        dashboard.put("dealerId", dealerId);
        
        return dashboard;
    }

    /**
     * Get dealer user roles usage information based on userRolesConfig
     */
    public Map<String, Object> getDealerUserRolesUsage() {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Getting user roles usage for dealer: {}", dealerId);
        
        Map<String, Object> usage = new HashMap<>();
        
        // Get dealer entity to access userRolesConfig
        Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + dealerId));
        
        String userRolesConfig = dealer.getUserRolesConfig();
        
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
                long currentCount = dealerStaffRepository.countByDealerIdAndRoleName(dealerId, roleName);
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
            long totalCurrentStaff = dealerStaffRepository.countByDealerIdAndUserActive(dealerId, true);
            int totalMaxStaff = roleLimits.values().stream().mapToInt(Integer::intValue).sum();
            usage.put("totalCurrentStaff", totalCurrentStaff);
            usage.put("totalMaxStaff", totalMaxStaff);
            usage.put("totalRemainingSlots", Math.max(0, totalMaxStaff - totalCurrentStaff));
            
        } catch (JsonProcessingException e) {
            log.error("Error parsing userRolesConfig for dealer {}: {}", dealerId, e.getMessage());
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

    private User createUserFromRequest(DealerStaffCreateRequestDTO request, Role role) {
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

    private DealerStaff createDealerStaffRecord(User user, Long dealerId) {
        DealerStaff dealerStaff = DealerStaff.builder()
                .user(user)
                .dealerId(dealerId)
                .districtCode("DEFAULT") // TODO: Get from current context
                .accessLevel(mapRoleToAccessLevel(user.getRole()))
                .build();
        
        return dealerStaffRepository.save(dealerStaff);
    }

    private DealerStaff getDealerStaffByIdAndDealer(Long staffId, Long dealerId) {
        return dealerStaffRepository.findByIdAndDealerId(staffId, dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + staffId));
    }

    private DealerStaff.DealerAccessLevel mapRoleToAccessLevel(Role role) {
        switch (role.getName()) {
            case RoleConstants.DEALER_ADMIN:
                return DealerStaff.DealerAccessLevel.OWNER;
            case RoleConstants.DEALER_EMPLOYEE:
                return DealerStaff.DealerAccessLevel.MANAGER;
            case RoleConstants.DEALER_TECHNICIAN:
                return DealerStaff.DealerAccessLevel.MECHANIC;
            default:
                return DealerStaff.DealerAccessLevel.VIEWER;
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
     * Validate that the dealer can add more staff based on their userRolesConfig
     */
    private void validateStaffLimitForDealer(Long dealerId, String roleName) {
        // Get dealer entity to access userRolesConfig
        Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + dealerId));
        
        String userRolesConfig = dealer.getUserRolesConfig();
        
        if (userRolesConfig == null || userRolesConfig.trim().isEmpty()) {
            log.warn("Dealer {} has no userRolesConfig set", dealerId);
            return; // Allow creation if no configuration is set
        }
        
        try {
            // Parse the JSON configuration
            Map<String, Integer> roleLimits = objectMapper.readValue(userRolesConfig, new TypeReference<Map<String, Integer>>() {});
            
            if (roleLimits.isEmpty()) {
                log.warn("Dealer {} has empty userRolesConfig", dealerId);
                return; // Allow creation if configuration is empty
            }
            
            // Get the limit for the specific role
            Integer maxRoleCount = roleLimits.get(roleName);
            
            if (maxRoleCount == null || maxRoleCount <= 0) {
                log.warn("Dealer {} has no limit set for role: {}", dealerId, roleName);
                return; // Allow creation if no limit is set for this role
            }
            
            // Count current active staff with this role
            long currentRoleCount = dealerStaffRepository.countByDealerIdAndRoleName(dealerId, roleName);
            
            if (currentRoleCount >= maxRoleCount) {
                throw new ConflictException(
                    String.format("Cannot create staff member with role '%s'. Dealer has reached the maximum allowed limit of %d users for this role. Current count: %d.", 
                        roleName, maxRoleCount, currentRoleCount)
                );
            }
            
            log.info("Dealer {} has {}/{} staff members with role '{}'. Staff creation allowed.", 
                dealerId, currentRoleCount, maxRoleCount, roleName);
                
        } catch (JsonProcessingException e) {
            log.error("Error parsing userRolesConfig for dealer {}: {}", dealerId, e.getMessage());
            throw new ConflictException("Invalid user roles configuration. Please contact support.");
        }
    }
}
