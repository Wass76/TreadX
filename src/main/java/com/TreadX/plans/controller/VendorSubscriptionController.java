package com.TreadX.plans.controller;

import com.TreadX.plans.dto.VendorSubscriptionRequestDTO;
import com.TreadX.plans.dto.VendorSubscriptionResponseDTO;
import com.TreadX.plans.service.VendorSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor-subscriptions")
@Tag(name = "Vendor Subscriptions", description = "Vendor subscription management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VendorSubscriptionController {
    
    private final VendorSubscriptionService vendorSubscriptionService;
    
    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create vendor subscription",
        description = "Creates a new vendor subscription. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the vendor subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VendorSubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VendorSubscriptionResponseDTO> createVendorSubscription(@RequestBody VendorSubscriptionRequestDTO request) {
        VendorSubscriptionResponseDTO subscription = vendorSubscriptionService.createVendorSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get vendor subscription by ID",
        description = "Retrieves a specific vendor subscription by its ID. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the vendor subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VendorSubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vendor subscription not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VendorSubscriptionResponseDTO> getVendorSubscriptionById(
            @Parameter(description = "ID of the vendor subscription", required = true) @PathVariable("id") Long id) {
        VendorSubscriptionResponseDTO subscription = vendorSubscriptionService.getVendorSubscriptionById(id);
        return ResponseEntity.ok(subscription);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get all vendor subscriptions",
        description = "Retrieves a paginated list of all vendor subscriptions. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved vendor subscriptions",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<VendorSubscriptionResponseDTO>> getAllVendorSubscriptions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<VendorSubscriptionResponseDTO> subscriptions = vendorSubscriptionService.getAllVendorSubscriptions(pageable);
        return ResponseEntity.ok(subscriptions);
    }
    
    @GetMapping("/vendor/{vendorId}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get vendor subscriptions by vendor ID",
        description = "Retrieves all subscriptions for a specific vendor. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved vendor subscriptions",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<VendorSubscriptionResponseDTO>> getVendorSubscriptionsByVendorId(
            @Parameter(description = "ID of the vendor", required = true) @PathVariable("vendorId") Long vendorId) {
        List<VendorSubscriptionResponseDTO> subscriptions = vendorSubscriptionService.getVendorSubscriptionsByVendorId(vendorId);
        return ResponseEntity.ok(subscriptions);
    }
    
    @GetMapping("/vendor/{vendorId}/active")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get active vendor subscription",
        description = "Retrieves the active subscription for a specific vendor. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active vendor subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VendorSubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No active subscription found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VendorSubscriptionResponseDTO> getActiveVendorSubscription(
            @Parameter(description = "ID of the vendor", required = true) @PathVariable("vendorId") Long vendorId) {
        VendorSubscriptionResponseDTO subscription = vendorSubscriptionService.getActiveVendorSubscription(vendorId);
        return ResponseEntity.ok(subscription);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Update vendor subscription",
        description = "Updates an existing vendor subscription. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the vendor subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VendorSubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vendor subscription not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VendorSubscriptionResponseDTO> updateVendorSubscription(
            @Parameter(description = "ID of the vendor subscription", required = true) @PathVariable("id") Long id,
            @RequestBody VendorSubscriptionRequestDTO request) {
        VendorSubscriptionResponseDTO subscription = vendorSubscriptionService.updateVendorSubscription(id, request);
        return ResponseEntity.ok(subscription);
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Cancel vendor subscription",
        description = "Cancels an active vendor subscription. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully cancelled the vendor subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VendorSubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vendor subscription not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VendorSubscriptionResponseDTO> cancelVendorSubscription(
            @Parameter(description = "ID of the vendor subscription", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason) {
        VendorSubscriptionResponseDTO subscription = vendorSubscriptionService.cancelVendorSubscription(id, reason);
        return ResponseEntity.ok(subscription);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Delete vendor subscription",
        description = "Deletes a vendor subscription from the system. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the vendor subscription"),
        @ApiResponse(responseCode = "404", description = "Vendor subscription not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteVendorSubscription(
            @Parameter(description = "ID of the vendor subscription", required = true) @PathVariable("id") Long id) {
        vendorSubscriptionService.deleteVendorSubscription(id);
        return ResponseEntity.noContent().build();
    }
} 