package com.TreadX.plans.controller;

import com.TreadX.plans.dto.SubscriptionPlanRequestDTO;
import com.TreadX.plans.dto.SubscriptionPlanResponseDTO;
import com.TreadX.plans.service.SubscriptionPlanService;
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
@RequestMapping("/api/v1/subscription-plans")
@Tag(name = "Subscription Plans", description = "Subscription plans management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SubscriptionPlanController {
    
    private final SubscriptionPlanService subscriptionPlanService;
    
    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Create subscription plan",
        description = "Creates a new subscription plan. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the subscription plan",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionPlanResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionPlanResponseDTO> createSubscriptionPlan(@RequestBody SubscriptionPlanRequestDTO request) {
        SubscriptionPlanResponseDTO plan = subscriptionPlanService.createSubscriptionPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get subscription plan by ID",
        description = "Retrieves a specific subscription plan by its ID. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the subscription plan",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionPlanResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Subscription plan not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionPlanResponseDTO> getSubscriptionPlanById(
            @Parameter(description = "ID of the subscription plan", required = true) @PathVariable("id") Long id) {
        SubscriptionPlanResponseDTO plan = subscriptionPlanService.getSubscriptionPlanById(id);
        return ResponseEntity.ok(plan);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get all subscription plans",
        description = "Retrieves a paginated list of all subscription plans. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription plans",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<SubscriptionPlanResponseDTO>> getAllSubscriptionPlans(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<SubscriptionPlanResponseDTO> plans = subscriptionPlanService.getAllSubscriptionPlans(pageable);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get active subscription plans",
        description = "Retrieves a paginated list of active subscription plans. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active subscription plans",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<SubscriptionPlanResponseDTO>> getActiveSubscriptionPlans(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<SubscriptionPlanResponseDTO> plans = subscriptionPlanService.getActiveSubscriptionPlans(pageable);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/by-user-count")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get subscription plans by user count",
        description = "Retrieves subscription plans that support the specified number of users. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subscription plans by user count",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SubscriptionPlanResponseDTO>> getPlansByUserCount(
            @Parameter(description = "Number of users", required = true) @RequestParam Integer userCount) {
        List<SubscriptionPlanResponseDTO> plans = subscriptionPlanService.getPlansByUserCount(userCount);
        return ResponseEntity.ok(plans);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Update subscription plan",
        description = "Updates an existing subscription plan. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the subscription plan",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SubscriptionPlanResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Subscription plan not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubscriptionPlanResponseDTO> updateSubscriptionPlan(
            @Parameter(description = "ID of the subscription plan", required = true) @PathVariable("id") Long id,
            @RequestBody SubscriptionPlanRequestDTO request) {
        SubscriptionPlanResponseDTO plan = subscriptionPlanService.updateSubscriptionPlan(id, request);
        return ResponseEntity.ok(plan);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Delete subscription plan",
        description = "Deletes a subscription plan from the system. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the subscription plan"),
        @ApiResponse(responseCode = "404", description = "Subscription plan not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteSubscriptionPlan(
            @Parameter(description = "ID of the subscription plan", required = true) @PathVariable("id") Long id) {
        subscriptionPlanService.deleteSubscriptionPlan(id);
        return ResponseEntity.noContent().build();
    }
} 