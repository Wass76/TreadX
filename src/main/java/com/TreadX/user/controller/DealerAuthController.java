package com.TreadX.user.controller;

import com.TreadX.user.dto.DealerLoginRequestDTO;
import com.TreadX.user.dto.DealerLoginResponseDTO;
import com.TreadX.user.service.DealerAuthService;
import com.TreadX.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dealer-auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dealer Authentication", description = "APIs for dealer authentication and account setup")
public class DealerAuthController {

    private final DealerAuthService dealerAuthService;

    @PostMapping("/login")
    @Operation(summary = "Dealer login", description = "Authenticate dealer and return JWT token")
    public ResponseEntity<ApiResponse<DealerLoginResponseDTO>> login(
            @Valid @RequestBody DealerLoginRequestDTO request) {
        
        log.info("Dealer login attempt for: {}", request.getEmail());
        DealerLoginResponseDTO response = dealerAuthService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Dealer login successful"));
    }

    @PostMapping("/setup-initial-account")
    @Operation(summary = "Setup initial dealer account", description = "Create initial dealer admin account after dealer creation")
    public ResponseEntity<ApiResponse<DealerLoginResponseDTO>> setupInitialAccount(
            @RequestParam String dealerUniqueId,
            @RequestParam String email) {
        
        log.info("Setting up initial account for dealer: {} with email: {}", dealerUniqueId, email);
        DealerLoginResponseDTO response = dealerAuthService.setupInitialAccount(dealerUniqueId, email);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Initial dealer account created successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change dealer password", description = "Allow dealer to change their password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        
        log.info("Password change requested for dealer");
        dealerAuthService.changePassword(currentPassword, newPassword);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Dealer logout", description = "Logout dealer and invalidate token")
    public ResponseEntity<ApiResponse<Void>> logout() {
        
        log.info("Dealer logout requested");
        dealerAuthService.logout();
        
        return ResponseEntity.ok(ApiResponse.success(null, "Dealer logged out successfully"));
    }
}
