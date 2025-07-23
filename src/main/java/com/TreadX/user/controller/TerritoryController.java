package com.TreadX.user.controller;

import com.TreadX.user.dto.TerritoryRequestDTO;
import com.TreadX.user.dto.TerritoryResponseDTO;
import com.TreadX.user.Enum.TerritoryLevel;
import com.TreadX.user.service.TerritoryService;
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
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/territories")
@RequiredArgsConstructor
@Tag(name = "Territory Management", description = "APIs for managing territories and their database connections")
public class TerritoryController {

    private final TerritoryService territoryService;

    /**
     * Create a new territory
     */
    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create a new territory",
        description = "Creates a new territory with database connection configuration. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Territory created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "409", description = "Territory code already exists")
    })
    public ResponseEntity<TerritoryResponseDTO> createTerritory(@Valid @RequestBody TerritoryRequestDTO request) {
        TerritoryResponseDTO createdTerritory = territoryService.createTerritory(request);
        return new ResponseEntity<>(createdTerritory, HttpStatus.CREATED);
    }

    /**
     * Get territory by code
     */
    @GetMapping("/{code}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get territory by code",
        description = "Retrieves territory information by its code. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territory retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Territory not found")
    })
    public ResponseEntity<TerritoryResponseDTO> getTerritoryByCode(
            @Parameter(description = "Territory code (e.g., N6B, LONDON)")
            @PathVariable String code) {
        TerritoryResponseDTO territory = territoryService.getTerritoryResponseByCode(code);
        return ResponseEntity.ok(territory);
    }

    /**
     * Get territory by code with hierarchical data
     */
    @GetMapping("/{code}/hierarchy")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get territory with hierarchy",
        description = "Retrieves territory information with child and ancestor territory data. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territory with hierarchy retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Territory not found")
    })
    public ResponseEntity<TerritoryResponseDTO> getTerritoryWithHierarchy(
            @Parameter(description = "Territory code (e.g., N6B, LONDON)")
            @PathVariable String code) {
        TerritoryResponseDTO territory = territoryService.getTerritoryByCodeWithHierarchy(code);
        return ResponseEntity.ok(territory);
    }

    /**
     * Get all active territories
     */
    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get all active territories",
        description = "Retrieves all active territories. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territories retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<List<TerritoryResponseDTO>> getAllTerritories() {
        List<TerritoryResponseDTO> territories = territoryService.getAllActiveTerritories();
        return ResponseEntity.ok(territories);
    }

    /**
     * Get territories by level
     */
    @GetMapping("/level/{level}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get territories by level",
        description = "Retrieves all active territories of a specific level (DISTRICT, CITY, PROVINCE, COUNTRY). Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territories retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<List<TerritoryResponseDTO>> getTerritoriesByLevel(
            @Parameter(description = "Territory level (DISTRICT, CITY, PROVINCE, COUNTRY)")
            @PathVariable TerritoryLevel level) {
        List<TerritoryResponseDTO> territories = territoryService.getTerritoriesByLevel(level);
        return ResponseEntity.ok(territories);
    }

    /**
     * Get child territories
     */
    @GetMapping("/{code}/children")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get child territories",
        description = "Retrieves all child territories of a specific territory. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Child territories retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Parent territory not found")
    })
    public ResponseEntity<List<TerritoryResponseDTO>> getChildTerritories(
            @Parameter(description = "Parent territory code")
            @PathVariable String code) {
        List<TerritoryResponseDTO> territories = territoryService.getChildTerritories(code);
        return ResponseEntity.ok(territories);
    }

    /**
     * Update territory
     */
    @PutMapping("/{code}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Update territory",
        description = "Updates an existing territory. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territory updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Territory not found")
    })
    public ResponseEntity<TerritoryResponseDTO> updateTerritory(
            @Parameter(description = "Territory code to update")
            @PathVariable String code,
            @Valid @RequestBody TerritoryRequestDTO request) {
        TerritoryResponseDTO updatedTerritory = territoryService.updateTerritory(code, request);
        return ResponseEntity.ok(updatedTerritory);
    }

    /**
     * Activate territory
     */
    @PostMapping("/{code}/activate")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Activate territory",
        description = "Activates a deactivated territory. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territory activated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Territory not found")
    })
    public ResponseEntity<TerritoryResponseDTO> activateTerritory(
            @Parameter(description = "Territory code to activate")
            @PathVariable String code) {
        TerritoryResponseDTO activatedTerritory = territoryService.activateTerritory(code);
        return ResponseEntity.ok(activatedTerritory);
    }

    /**
     * Deactivate territory
     */
    @PostMapping("/{code}/deactivate")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Deactivate territory",
        description = "Deactivates an active territory. Cannot deactivate if it has active child territories. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territory deactivated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TerritoryResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Territory not found"),
        @ApiResponse(responseCode = "409", description = "Cannot deactivate territory with active children")
    })
    public ResponseEntity<TerritoryResponseDTO> deactivateTerritory(
            @Parameter(description = "Territory code to deactivate")
            @PathVariable String code) {
        TerritoryResponseDTO deactivatedTerritory = territoryService.deactivateTerritory(code);
        return ResponseEntity.ok(deactivatedTerritory);
    }

    /**
     * Get all active territory codes
     */
    @GetMapping("/codes")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get all active territory codes",
        description = "Retrieves a list of all active territory codes. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territory codes retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<List<String>> getAllTerritoryCodes() {
        List<String> codes = territoryService.getAllActiveTerritoryCodes();
        return ResponseEntity.ok(codes);
    }

    /**
     * Get territory codes by level
     */
    @GetMapping("/codes/level/{level}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Get territory codes by level",
        description = "Retrieves territory codes of a specific level. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Territory codes retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<List<String>> getTerritoryCodesByLevel(
            @Parameter(description = "Territory level (DISTRICT, CITY, PROVINCE, COUNTRY)")
            @PathVariable TerritoryLevel level) {
        List<String> codes = territoryService.getTerritoryCodesByLevel(level);
        return ResponseEntity.ok(codes);
    }
}