package com.TreadX.plans.controller;

import com.TreadX.plans.dto.SubscriptionRequestDTO;
import com.TreadX.plans.dto.SubscriptionResponseDTO;
import com.TreadX.plans.service.SubscriptionService;
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
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create dealer subscription",
        description = "Creates a new dealer subscription. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the dealer subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(@RequestBody SubscriptionRequestDTO request) {
        SubscriptionResponseDTO subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get dealer subscription by ID",
        description = "Retrieves a specific dealer subscription by its ID. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the dealer subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "dealer subscription not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionById(
            @Parameter(description = "ID of the dealer subscription", required = true) @PathVariable("id") Long id) {
        SubscriptionResponseDTO subscription = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(subscription);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get all dealer subscriptions",
        description = "Retrieves a paginated list of all dealer subscriptions. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved dealer subscriptions",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<SubscriptionResponseDTO>> getAllSubscriptions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<SubscriptionResponseDTO> subscriptions = subscriptionService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(subscriptions);
    }
    
    @GetMapping("/dealer/{dealerId}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get dealer subscriptions by dealer ID",
        description = "Retrieves all subscriptions for a specific dealer. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved dealer subscriptions",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SubscriptionResponseDTO>> getSubscriptionsByDealerId(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("dealerId") Long dealerId) {
        List<SubscriptionResponseDTO> subscriptions = subscriptionService.getSubscriptionsByDealerId(dealerId);
        return ResponseEntity.ok(subscriptions);
    }
    
    @GetMapping("/dealer/{dealerId}/active")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get active dealer subscription",
        description = "Retrieves the active subscription for a specific dealer. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active dealer subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No active subscription found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionResponseDTO> getActiveSubscription(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("dealerId") Long dealerId) {
        SubscriptionResponseDTO subscription = subscriptionService.getActiveSubscription(dealerId);
        return ResponseEntity.ok(subscription);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Update dealer subscription",
        description = "Updates an existing dealer subscription. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the dealer subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Dealer subscription not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionResponseDTO> updateSubscription(
            @Parameter(description = "ID of the dealer subscription", required = true) @PathVariable("id") Long id,
            @RequestBody SubscriptionRequestDTO request) {
        SubscriptionResponseDTO subscription = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(subscription);
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Cancel dealer subscription",
        description = "Cancels an active dealer subscription. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully cancelled the dealer subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Dealer subscription not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionResponseDTO> cancelSubscription(
            @Parameter(description = "ID of the dealer subscription", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason) {
        SubscriptionResponseDTO subscription = subscriptionService.cancelSubscription(id, reason);
        return ResponseEntity.ok(subscription);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Delete dealer subscription",
        description = "Deletes a dealer subscription from the system. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the dealer subscription"),
        @ApiResponse(responseCode = "404", description = "Dealer subscription not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteSubscription(
            @Parameter(description = "ID of the dealer subscription", required = true) @PathVariable("id") Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
} 