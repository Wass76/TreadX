package com.TreadX.user.controller;

import com.TreadX.user.dto.DealerStaffCreateRequestDTO;
import com.TreadX.user.dto.DealerStaffResponseDTO;
import com.TreadX.user.dto.DealerStaffUpdateRequestDTO;
import com.TreadX.user.service.DealerPortalService;
import com.TreadX.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dealer-portal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dealer Portal", description = "APIs for dealer self-service portal")
public class DealerPortalController {

    private final DealerPortalService dealerPortalService;

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('DEALER_ADMIN', 'DEALER_EMPLOYEE')")
    @Operation(summary = "Get dealer staff list", description = "Retrieve all staff members for the authenticated dealer with optional pagination")
    public ResponseEntity<ApiResponse<Page<DealerStaffResponseDTO>>> getDealerStaff(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        
        log.info("Dealer staff list requested");
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerStaffResponseDTO> staff = dealerPortalService.getDealerStaff(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(staff, "Dealer staff retrieved successfully"));
    }

    @PostMapping("/staff")
    @PreAuthorize("hasRole('DEALER_ADMIN')")
    @Operation(summary = "Create new staff member", description = "Create a new staff account for the dealer")
    public ResponseEntity<ApiResponse<DealerStaffResponseDTO>> createStaffMember(
            @Valid @RequestBody DealerStaffCreateRequestDTO request) {
        
        log.info("Creating new dealer staff member: {}", request.getEmail());
        DealerStaffResponseDTO newStaff = dealerPortalService.createStaffMember(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newStaff, "Dealer staff member created successfully"));
    }

    @PutMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('DEALER_ADMIN')")
    @Operation(summary = "Update dealer staff member", description = "Update dealer staff member information")
    public ResponseEntity<ApiResponse<DealerStaffResponseDTO>> updateStaffMember(
            @PathVariable Long staffId,
            @Valid @RequestBody DealerStaffUpdateRequestDTO request) {
        
        log.info("Updating dealer staff member: {}", staffId);
        DealerStaffResponseDTO updatedStaff = dealerPortalService.updateStaffMember(staffId, request);
        
        return ResponseEntity.ok(ApiResponse.success(updatedStaff, "Staff member updated successfully"));
    }

    @DeleteMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('DEALER_ADMIN')")
    @Operation(summary = "Deactivate staff member", description = "Deactivate a staff member account")
    public ResponseEntity<ApiResponse<Void>> deactivateStaffMember(@PathVariable Long staffId) {
        
        log.info("Deactivating staff member: {}", staffId);
        dealerPortalService.deactivateStaffMember(staffId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Staff member deactivated successfully"));
    }

    @DeleteMapping("/staff/{staffId}/delete")
    @PreAuthorize("hasRole('DEALER_ADMIN')")
    @Operation(summary = "Delete staff member permanently", description = "Permanently delete a staff member account and all associated data")
    public ResponseEntity<ApiResponse<Void>> deleteStaffMember(@PathVariable Long staffId) {
        
        log.info("Deleting staff member permanently: {}", staffId);
        dealerPortalService.deleteStaffMember(staffId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Staff member deleted permanently"));
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('DEALER_ADMIN', 'DEALER_EMPLOYEE')")
    @Operation(summary = "Get staff member details", description = "Retrieve detailed information about a specific staff member")
    public ResponseEntity<ApiResponse<DealerStaffResponseDTO>> getStaffMember(@PathVariable Long staffId) {
        
        log.info("Getting staff member details: {}", staffId);
        DealerStaffResponseDTO staff = dealerPortalService.getStaffMember(staffId);
        
        return ResponseEntity.ok(ApiResponse.success(staff, "Staff member details retrieved successfully"));
    }

    @GetMapping("/staff/roles")
    @PreAuthorize("hasRole('DEALER_ADMIN')")
    @Operation(summary = "Get available roles", description = "Get list of available roles for staff assignment")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableRoles() {
        
        log.info("Getting available roles for dealer staff");
        List<String> roles = dealerPortalService.getAvailableRoles();
        
        return ResponseEntity.ok(ApiResponse.success(roles, "Available roles retrieved successfully"));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('DEALER_ADMIN', 'DEALER_EMPLOYEE', 'DEALER_TECHNICIAN')")
    @Operation(summary = "Get dealer dashboard", description = "Get dealer dashboard information and statistics")
    public ResponseEntity<ApiResponse<Object>> getDealerDashboard() {
        
        log.info("Getting dealer dashboard");
        Object dashboard = dealerPortalService.getDealerDashboard();
        
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dealer dashboard retrieved successfully"));
    }
}
