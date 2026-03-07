package com.TreadX.district.dealer.controller;

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

import com.TreadX.district.dealer.dto.DealerCreationRequestDTO;
import com.TreadX.district.dealer.dto.DealerCreationResponseDTO;
import com.TreadX.district.dealer.service.EnhancedDealerService;

@RestController
@RequestMapping("/api/v1/enhanced-dealers")
@Tag(name = "Enhanced Dealers", description = "Enhanced dealer creation with user access and subscription management")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EnhancedDealerController {
    
    private final EnhancedDealerService enhancedDealerService;
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create dealer with user access and subscription",
        description = "Creates a new dealer with user access management and subscription plan. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the dealer with access and subscription",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerCreationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerCreationResponseDTO> createDealerWithAccessAndSubscription(@RequestBody DealerCreationRequestDTO request) {
        DealerCreationResponseDTO response = enhancedDealerService.createDealerWithAccessAndSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 