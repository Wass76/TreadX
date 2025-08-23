package com.TreadX.user.service;

import com.TreadX.config.JwtService;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.user.dto.VendorLoginRequestDTO;
import com.TreadX.user.dto.VendorLoginResponseDTO;
import com.TreadX.user.entity.Role;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.VendorStaff;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.VendorStaffRepository;
import com.TreadX.user.config.RoleConstants;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.utils.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VendorAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final VendorStaffRepository vendorStaffRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authenticate vendor and return JWT token
     */
    public VendorLoginResponseDTO login(VendorLoginRequestDTO request) {
        log.info("Authenticating vendor: {}", request.getEmail());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Verify user is a vendor staff member
            VendorStaff vendorStaff = vendorStaffRepository.findByUserEmail(request.getEmail())
                    .orElseThrow(() -> new UnAuthorizedException("User is not a vendor staff member"));
            
            // Get vendor information
            Vendor vendor = vendorRepository.findById(vendorStaff.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
            
            // Generate JWT token
            String token = jwtService.generateToken(user);
            
            // Build response
            VendorLoginResponseDTO response = new VendorLoginResponseDTO();
            response.setToken(token);
            response.setRefreshToken(""); // No refresh token for now
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().getName());
            response.setVendorId(vendor.getId().toString());
            response.setVendorName(vendor.getBusinessName());
            response.setExpiresIn(3600L); // 1 hour
            
            log.info("Vendor login successful: {}", user.getEmail());
            return response;
            
        } catch (Exception e) {
            log.error("Vendor login failed for: {}", request.getEmail(), e);
            throw new UnAuthorizedException("Invalid email or password");
        }
    }

    /**
     * Setup initial vendor admin account after vendor creation
     */
    public VendorLoginResponseDTO setupInitialAccount(String vendorUniqueId, String email) {
        log.info("Setting up initial account for vendor: {} with email: {}", vendorUniqueId, email);
        
        // Find vendor by unique ID
        Vendor vendor = vendorRepository.findByVendorUniqueId(vendorUniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorUniqueId));
        
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UnAuthorizedException("User with this email already exists");
        }
        
        // Get VENDOR_ADMIN role
        Role vendorAdminRole = roleRepository.findByName(RoleConstants.VENDOR_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("VENDOR_ADMIN role not found"));
        
        // Generate random password
        String generatedPassword = generateRandomPassword();
        
        // Create user
        User user = User.builder()
                .firstName("Vendor")
                .lastName("Admin")
                .email(email)
                .password(passwordEncoder.encode(generatedPassword))
                .role(vendorAdminRole)
                .position("Vendor Administrator")
                .isActive(true)
                .isSystem(false)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create vendor staff record
        VendorStaff vendorStaff = VendorStaff.builder()
                .user(savedUser)
                .vendorId(vendor.getId())
                .districtCode("DEFAULT")
                .accessLevel(VendorStaff.VendorAccessLevel.OWNER)
                .build();
        
        vendorStaffRepository.save(vendorStaff);
        
        // Log the generated password (in production, send via email)
        log.info("Initial vendor account created for {} with password: {}", email, generatedPassword);
        
        // Return login response
        VendorLoginResponseDTO response = new VendorLoginResponseDTO();
        response.setEmail(email);
        response.setRole(vendorAdminRole.getName());
        response.setVendorId(vendor.getId().toString());
        response.setVendorName(vendor.getBusinessName());
        response.setExpiresIn(3600L);
        
        return response;
    }

    /**
     * Change vendor password
     */
    public void changePassword(String currentPassword, String newPassword) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnAuthorizedException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", currentUserEmail);
    }

    /**
     * Logout vendor
     */
    public void logout() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Logging out vendor: {}", currentUserEmail);
        
        // In a real implementation, you might want to blacklist the token
        // For now, we just log the logout
        SecurityContextHolder.clearContext();
    }

    /**
     * Generate random password for initial account setup
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
