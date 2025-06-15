package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.service.DealerService;
import com.TreadX.user.service.AuthorizationService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dealers")
@Tag(name = "Dealers", description = "Dealers management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DealerController {

    private final DealerService dealerService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get all dealers",
        description = "Retrieves a paginated list of all dealers in the system. Requires SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved dealers",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<DealerResponseDTO>> getAllDealers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerResponseDTO> dealers = dealerService.getAllDealers(pageable);
        return new ResponseEntity<>(dealers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isDealerOwner(#id)")
    @Operation(
        summary = "Get dealer by ID",
        description = "Retrieves a specific dealer by its ID. Requires SALES_MANAGER role or ownership of the dealer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the dealer",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Dealer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerResponseDTO> getDealerById(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("id") Long id) {
        if (!authorizationService.hasAccessToDealer(id, "READ")) {
            throw new AccessDeniedException("You don't have permission to read this dealer");
        }
        DealerResponseDTO dealer = dealerService.getDealerById(id);
        return new ResponseEntity<>(dealer, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create new dealer",
        description = "Creates a new dealer in the system. Requires SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the dealer",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerResponseDTO> createDealer(
            @Parameter(description = "Dealer data", required = true) @RequestBody DealerRequestDTO dealer) {
        DealerResponseDTO createdDealer = dealerService.createDealer(dealer , null);
        return new ResponseEntity<>(createdDealer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isDealerOwner(#id)")
    @Operation(
        summary = "Update dealer",
        description = "Updates an existing dealer. Requires SALES_MANAGER role or ownership of the dealer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the dealer",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Dealer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerResponseDTO> updateDealer(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Updated dealer data", required = true) @RequestBody DealerRequestDTO dealerDetails) {
        if (!authorizationService.hasAccessToDealer(id, "UPDATE")) {
            throw new AccessDeniedException("You don't have permission to update this dealer");
        }
        DealerResponseDTO updatedDealer = dealerService.updateDealer(id, dealerDetails);
        return new ResponseEntity<>(updatedDealer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isDealerOwner(#id)")
    @Operation(
        summary = "Delete dealer",
        description = "Deletes a dealer from the system. Requires SALES_MANAGER role or ownership of the dealer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the dealer"),
        @ApiResponse(responseCode = "404", description = "Dealer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteDealer(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("id") Long id) {
        if (!authorizationService.hasAccessToDealer(id, "DELETE")) {
            throw new AccessDeniedException("You don't have permission to delete this dealer");
        }
        dealerService.deleteDealer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Search dealers",
        description = "Searches for dealers based on a query string. Requires SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching dealers",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<DealerResponseDTO>> searchDealers(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerResponseDTO> dealers = dealerService.searchDealers(query, pageable);
        return new ResponseEntity<>(dealers, HttpStatus.OK);
    }
} 