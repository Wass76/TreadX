package com.TreadX.user.controller;

import com.TreadX.user.dto.VendorStaffCreateRequestDTO;
import com.TreadX.user.dto.VendorStaffResponseDTO;
import com.TreadX.user.dto.VendorStaffUpdateRequestDTO;
import com.TreadX.user.service.VendorPortalService;
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
@RequestMapping("/api/vendor-portal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vendor Portal", description = "APIs for vendor self-service portal")
public class VendorPortalController {

    private final VendorPortalService vendorPortalService;

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE')")
    @Operation(summary = "Get vendor staff list", description = "Retrieve all staff members for the authenticated vendor with optional pagination")
    public ResponseEntity<ApiResponse<Page<VendorStaffResponseDTO>>> getVendorStaff(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        
        log.info("Vendor staff list requested");
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<VendorStaffResponseDTO> staff = vendorPortalService.getVendorStaff(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(staff, "Vendor staff retrieved successfully"));
    }

    @PostMapping("/staff")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    @Operation(summary = "Create new staff member", description = "Create a new staff account for the vendor")
    public ResponseEntity<ApiResponse<VendorStaffResponseDTO>> createStaffMember(
            @Valid @RequestBody VendorStaffCreateRequestDTO request) {
        
        log.info("Creating new staff member: {}", request.getEmail());
        VendorStaffResponseDTO newStaff = vendorPortalService.createStaffMember(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newStaff, "Staff member created successfully"));
    }

    @PutMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    @Operation(summary = "Update staff member", description = "Update staff member information")
    public ResponseEntity<ApiResponse<VendorStaffResponseDTO>> updateStaffMember(
            @PathVariable Long staffId,
            @Valid @RequestBody VendorStaffUpdateRequestDTO request) {
        
        log.info("Updating staff member: {}", staffId);
        VendorStaffResponseDTO updatedStaff = vendorPortalService.updateStaffMember(staffId, request);
        
        return ResponseEntity.ok(ApiResponse.success(updatedStaff, "Staff member updated successfully"));
    }

    @DeleteMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    @Operation(summary = "Deactivate staff member", description = "Deactivate a staff member account")
    public ResponseEntity<ApiResponse<Void>> deactivateStaffMember(@PathVariable Long staffId) {
        
        log.info("Deactivating staff member: {}", staffId);
        vendorPortalService.deactivateStaffMember(staffId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Staff member deactivated successfully"));
    }

    @DeleteMapping("/staff/{staffId}/delete")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    @Operation(summary = "Delete staff member permanently", description = "Permanently delete a staff member account and all associated data")
    public ResponseEntity<ApiResponse<Void>> deleteStaffMember(@PathVariable Long staffId) {
        
        log.info("Deleting staff member permanently: {}", staffId);
        vendorPortalService.deleteStaffMember(staffId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Staff member deleted permanently"));
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE')")
    @Operation(summary = "Get staff member details", description = "Retrieve detailed information about a specific staff member")
    public ResponseEntity<ApiResponse<VendorStaffResponseDTO>> getStaffMember(@PathVariable Long staffId) {
        
        log.info("Getting staff member details: {}", staffId);
        VendorStaffResponseDTO staff = vendorPortalService.getStaffMember(staffId);
        
        return ResponseEntity.ok(ApiResponse.success(staff, "Staff member details retrieved successfully"));
    }

    @GetMapping("/staff/roles")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    @Operation(summary = "Get available roles", description = "Get list of available roles for staff assignment")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableRoles() {
        
        log.info("Getting available roles for vendor staff");
        List<String> roles = vendorPortalService.getAvailableRoles();
        
        return ResponseEntity.ok(ApiResponse.success(roles, "Available roles retrieved successfully"));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN')")
    @Operation(summary = "Get vendor dashboard", description = "Get vendor dashboard information and statistics")
    public ResponseEntity<ApiResponse<Object>> getVendorDashboard() {
        
        log.info("Getting vendor dashboard");
        Object dashboard = vendorPortalService.getVendorDashboard();
        
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Vendor dashboard retrieved successfully"));
    }
}
