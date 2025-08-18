package com.TreadX.district.vendors.controller;

import com.TreadX.district.vendors.dto.VendorCreationRequestDTO;
import com.TreadX.district.vendors.dto.VendorCreationResponseDTO;
import com.TreadX.district.vendors.service.EnhancedVendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enhanced-vendors")
@Tag(name = "Enhanced Vendors", description = "Enhanced vendor creation with user access and subscription management")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EnhancedVendorController {
    
    private final EnhancedVendorService enhancedVendorService;
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create vendor with user access and subscription",
        description = "Creates a new vendor with user access management and subscription plan. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the vendor with access and subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VendorCreationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VendorCreationResponseDTO> createVendorWithAccessAndSubscription(@RequestBody VendorCreationRequestDTO request) {
        VendorCreationResponseDTO response = enhancedVendorService.createVendorWithAccessAndSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 