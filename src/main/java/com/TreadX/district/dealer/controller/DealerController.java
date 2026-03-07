package com.TreadX.district.dealer.controller;

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

import com.TreadX.district.dealer.dto.DealerRequestDTO;
import com.TreadX.district.dealer.dto.DealerResponseDTO;
import com.TreadX.district.dealer.enums.DealerStatus;
import com.TreadX.district.dealer.service.DealerService;

@RestController
@RequestMapping("/api/v1/dealers")
@Tag(name = "Dealers", description = "Dealers management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DealerController {

    private final DealerService dealerService;

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get all dealers",
        description = "Retrieves a paginated list of all dealers in the system. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
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
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get dealer by ID",
        description = "Retrieves a specific dealer by its ID. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
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
        DealerResponseDTO dealer = dealerService.getDealerById(id);
        return new ResponseEntity<>(dealer, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Create new dealer",
        description = "Creates a new dealer from a contacted lead. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the dealer",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerResponseDTO> createDealer(@RequestBody DealerRequestDTO request) {
        DealerResponseDTO dealer = dealerService.createDealer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dealer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Update dealer",
        description = "Updates an existing dealer. Requires PLATFORM_ADMIN or SALES_MANAGER role."
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
            @RequestBody DealerRequestDTO request) {
        DealerResponseDTO dealer = dealerService.updateDealer(id, request);
        return new ResponseEntity<>(dealer, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Partially update dealer",
        description = "Partially updates an existing dealer. Only fields that are present in the request will be updated. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully partially updated the dealer",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Dealer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerResponseDTO> updateDealerPartial(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("id") Long id,
            @RequestBody DealerRequestDTO request) {
        DealerResponseDTO dealer = dealerService.updateDealerPartial(id, request);
        return new ResponseEntity<>(dealer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(
        summary = "Delete dealer",
        description = "Deletes a dealer from the system. Requires PLATFORM_ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the dealer"),
        @ApiResponse(responseCode = "404", description = "Dealer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteDealer(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("id") Long id) {
        dealerService.deleteDealer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Search dealers",
        description = "Searches dealers by legal name, business name, email, or phone number. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully searched dealers",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<DealerResponseDTO>> searchDealers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerResponseDTO> dealers = dealerService.searchDealers(query, pageable);
        return ResponseEntity.ok(dealers);
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get dealers by status",
        description = "Retrieves a paginated list of dealers filtered by status. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved dealers by status",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status value"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<DealerResponseDTO>> getDealersByStatus(
            @RequestParam DealerStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerResponseDTO> dealers = dealerService.getDealersByStatus(status, pageable);
        return ResponseEntity.ok(dealers);
    }
} 