package com.TreadX.user.controller;

import com.TreadX.user.dto.VendorLoginRequestDTO;
import com.TreadX.user.dto.VendorLoginResponseDTO;
import com.TreadX.user.service.VendorAuthService;
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
@RequestMapping("/api/vendor-auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vendor Authentication", description = "APIs for vendor authentication and account setup")
public class VendorAuthController {

    private final VendorAuthService vendorAuthService;

    @PostMapping("/login")
    @Operation(summary = "Vendor login", description = "Authenticate vendor and return JWT token")
    public ResponseEntity<ApiResponse<VendorLoginResponseDTO>> login(
            @Valid @RequestBody VendorLoginRequestDTO request) {
        
        log.info("Vendor login attempt for: {}", request.getEmail());
        VendorLoginResponseDTO response = vendorAuthService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor login successful"));
    }

    @PostMapping("/setup-initial-account")
    @Operation(summary = "Setup initial vendor account", description = "Create initial vendor admin account after vendor creation")
    public ResponseEntity<ApiResponse<VendorLoginResponseDTO>> setupInitialAccount(
            @RequestParam String vendorUniqueId,
            @RequestParam String email) {
        
        log.info("Setting up initial account for vendor: {} with email: {}", vendorUniqueId, email);
        VendorLoginResponseDTO response = vendorAuthService.setupInitialAccount(vendorUniqueId, email);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Initial vendor account created successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change vendor password", description = "Allow vendor to change their password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        
        log.info("Password change requested for vendor");
        vendorAuthService.changePassword(currentPassword, newPassword);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Vendor logout", description = "Logout vendor and invalidate token")
    public ResponseEntity<ApiResponse<Void>> logout() {
        
        log.info("Vendor logout requested");
        vendorAuthService.logout();
        
        return ResponseEntity.ok(ApiResponse.success(null, "Vendor logged out successfully"));
    }
}
