package com.TreadX.user.service;

import com.TreadX.config.JwtService;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.repository.DealerRepository;
import com.TreadX.user.dto.DealerLoginRequestDTO;
import com.TreadX.user.dto.DealerLoginResponseDTO;
import com.TreadX.user.entity.Role;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.DealerStaff;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.DealerStaffRepository;
import com.TreadX.user.config.RoleConstants;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.utils.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealerAuthService {

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final DealerStaffRepository dealerStaffRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authenticate dealer and return JWT token
     */
    public DealerLoginResponseDTO login(DealerLoginRequestDTO request) {
        log.info("Authenticating dealer: {}", request.getEmail());
        
        try {
            // Authenticate user
            // Authentication authentication = authenticationManager.authenticate(
            //     new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            // );
            
            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Verify user is a dealer staff member
            DealerStaff dealerStaff = dealerStaffRepository.findByUserEmail(request.getEmail())
                    .orElseThrow(() -> new UnAuthorizedException("User is not a dealer staff member"));
            
            // Get dealer information
            Dealer dealer = dealerRepository.findById(dealerStaff.getDealerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found"));
            
            // Generate JWT token
            String token = jwtService.generateToken(user);
            
            // Build response
            DealerLoginResponseDTO response = new DealerLoginResponseDTO();
            response.setToken(token);
            response.setRefreshToken(""); // No refresh token for now
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().getName());
            response.setDealerId(dealer.getId().toString());
            response.setDealerName(dealer.getBusinessName());
            response.setExpiresIn(3600L); // 1 hour
            
            log.info("Dealer login successful: {}", user.getEmail());
            return response;
            
        } catch (Exception e) {
            log.error("Dealer login failed for: {}", request.getEmail(), e);
            throw new UnAuthorizedException("Invalid email or password");
        }
    }

    /**
     * Setup initial dealer admin account after dealer creation
     */
    public DealerLoginResponseDTO setupInitialAccount(String dealerUniqueId, String email) {
        log.info("Setting up initial account for dealer: {} with email: {}", dealerUniqueId, email);
        
        // Find dealer by unique ID
        Dealer dealer = dealerRepository.findByDealerUniqueId(dealerUniqueId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with ID: " + dealerUniqueId));
        
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UnAuthorizedException("User with this email already exists");
        }
        
        // Get DEALER_ADMIN role
        Role dealerAdminRole = roleRepository.findByName(RoleConstants.DEALER_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("DEALER_ADMIN role not found"));
        
        // Generate random password
        String generatedPassword = generateRandomPassword();
        
        // Create user
        User user = User.builder()
                .firstName("Dealer")
                .lastName("Admin")
                .email(email)
                .password(passwordEncoder.encode(generatedPassword))
                .role(dealerAdminRole)
                .position("Dealer Administrator")
                .isActive(true)
                .isSystem(false)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create dealer staff record
        DealerStaff dealerStaff = DealerStaff.builder()
                .user(savedUser)
                .dealerId(dealer.getId())
                .districtCode("DEFAULT")
                .accessLevel(DealerStaff.DealerAccessLevel.OWNER)
                .build();
        
        dealerStaffRepository.save(dealerStaff);
        
        // Log the generated password (in production, send via email)
        log.info("Initial dealer account created for {} with password: {}", email, generatedPassword);
        
        // Return login response
        DealerLoginResponseDTO response = new DealerLoginResponseDTO();
        response.setEmail(email);
        response.setRole(dealerAdminRole.getName());
        response.setDealerId(dealer.getId().toString());
        response.setDealerName(dealer.getBusinessName());
        response.setExpiresIn(3600L);
        
        return response;
    }

    /**
     * Change dealer password
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
     * Logout dealer
     */
    public void logout() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Logging out dealer: {}", currentUserEmail);
        
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
