package com.TreadX.user.controller;

import com.TreadX.user.dto.UserTerritoryRequestDTO;
import com.TreadX.user.dto.UserTerritoryResponseDTO;
import com.TreadX.user.entity.TerritoryLevel;
import com.TreadX.user.service.UserTerritoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user-territories")
@RequiredArgsConstructor
public class UserTerritoryController {
    
    private final UserTerritoryService userTerritoryService;
    
    /**
     * Assign territories to a user
     */
    @PostMapping("/users/{userId}/territories")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Assign territory to user",
        description = "Assigns a territory (city, province, or country) to a user. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Territory assigned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<UserTerritoryResponseDTO> assignTerritory(
            @PathVariable Long userId,
            @RequestParam TerritoryLevel level,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long provinceId,
            @RequestParam(required = false) Long countryId) {
        // Build the request DTO using the new base fields
        UserTerritoryRequestDTO dto = new UserTerritoryRequestDTO();
        dto.setUserId(userId);
        dto.setTerritoryLevel(level);
        dto.setBaseCountryId(countryId);
        dto.setBaseProvinceId(provinceId);
        dto.setBaseCityId(cityId);
        List<UserTerritoryRequestDTO> requestList = Collections.singletonList(dto);
        userTerritoryService.assignTerritoriesToUser(userId, requestList);
        // Fetch the latest territories and return the most recent one
        List<UserTerritoryResponseDTO> territories = userTerritoryService.getUserTerritories(userId);
        UserTerritoryResponseDTO territory = territories.isEmpty() ? null : territories.get(territories.size() - 1);
        return new ResponseEntity<>(territory, HttpStatus.CREATED);
    }
    
    /**
     * Get all territories for a user
     */
    @GetMapping("/users/{userId}/territories")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or #userId == authentication.principal.id")
    @Operation(
        summary = "Get all territories for a user",
        description = "Retrieves all territory assignments for a specific user. Requires PLATFORM_ADMIN, SALES_MANAGER, or ownership."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user territories",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<List<UserTerritoryResponseDTO>> getUserTerritories(
            @PathVariable Long userId) {
        
        List<UserTerritoryResponseDTO> territories = userTerritoryService.getUserTerritories(userId);
        
        return new ResponseEntity<>(territories, HttpStatus.OK);
    }
    
    /**
     * Get current user's territories
     */
    @GetMapping("/my-territories")
    @Operation(
        summary = "Get current user's territories",
        description = "Retrieves all territory assignments for the currently authenticated user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved current user's territories",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTerritoryResponseDTO.class)))
    })
    public ResponseEntity<List<UserTerritoryResponseDTO>> getMyTerritories() {
        List<UserTerritoryResponseDTO> territories = userTerritoryService.getCurrentUserTerritories();
        
        return new ResponseEntity<>(territories, HttpStatus.OK);
    }
    
    /**
     * Deactivate a territory assignment
     */
    @DeleteMapping("/{territoryId}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Deactivate a territory assignment",
        description = "Deactivates a territory assignment by its ID. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Territory deactivated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Territory not found")
    })
    public ResponseEntity<Void> deactivateTerritory(@PathVariable Long territoryId) {
        userTerritoryService.deactivateTerritory(territoryId);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * Get accessible city IDs for current user
     */
    @GetMapping("/accessible-cities")
    @Operation(
        summary = "Get accessible city IDs for current user",
        description = "Retrieves a list of city IDs that the current user has access to."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved accessible city IDs",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<List<Long>> getAccessibleCities() {
        List<Long> cityIds = userTerritoryService.getAccessibleCities();
        return new ResponseEntity<>(cityIds, HttpStatus.OK);
    }
    
    /**
     * Check if user has access to a specific location
     */
    @PostMapping("/check-access")
    @Operation(
        summary = "Check location access",
        description = "Checks if the current user has access to a specific location (city, province, or country)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access check result",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> checkLocationAccess(
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long provinceId,
            @RequestParam(required = false) Long countryId) {
        
        boolean hasAccess = userTerritoryService.checkLocationAccess(cityId, provinceId, countryId);
        
        return new ResponseEntity<>(hasAccess, HttpStatus.OK);
    }
    
    /**
     * Get territories for a user by level
     */
    @GetMapping("/users/{userId}/territories/level/{level}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or #userId == authentication.principal.id")
    @Operation(
        summary = "Get user territories by level",
        description = "Retrieves all territory assignments for a user at a specific level (city, province, or country). Requires PLATFORM_ADMIN, SALES_MANAGER, or ownership."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user territories by level",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<List<UserTerritoryResponseDTO>> getUserTerritoriesByLevel(
            @PathVariable Long userId,
            @PathVariable TerritoryLevel level) {
        
        List<UserTerritoryResponseDTO> territories = userTerritoryService.getUserTerritoriesByLevel(userId, level);
        
        return new ResponseEntity<>(territories, HttpStatus.OK);
    }
    
    /**
     * Check if user has territory assignments
     */
    @GetMapping("/users/{userId}/has-territories")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or #userId == authentication.principal.id")
    @Operation(
        summary = "Check if user has territory assignments",
        description = "Checks if a user has any active territory assignments. Requires PLATFORM_ADMIN, SALES_MANAGER, or ownership."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check result",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<Boolean> hasTerritoryAssignments(@PathVariable Long userId) {
        boolean hasTerritories = userTerritoryService.hasTerritoryAssignments(userId);
        
        return new ResponseEntity<>(hasTerritories, HttpStatus.OK);
    }
} 