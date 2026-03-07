package com.TreadX.district.dealer.DealerCustomer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.TreadX.district.dealer.DealerCustomer.dto.DealerCustomerRequestDTO;
import com.TreadX.district.dealer.DealerCustomer.dto.DealerCustomerResponseDTO;
import com.TreadX.district.dealer.DealerCustomer.service.DealerCustomerService;

@RestController
@RequestMapping("/api/v1/dealerDealerCustomers")
@RequiredArgsConstructor
@Tag(name = "DealerCustomer Management", description = "APIs for managing dealerDealerCustomers in the vendor portal")
public class DealerCustomerController {

    private final DealerCustomerService dealerCustomerService;

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Create a new dealerDealerCustomer", description = "Create a new dealerDealerCustomer for a vendor")
    public ResponseEntity<DealerCustomerResponseDTO> createDealerCustomer(@Valid @RequestBody DealerCustomerRequestDTO requestDTO) {
        DealerCustomerResponseDTO dealerDealerCustomer = dealerCustomerService.createDealerCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dealerDealerCustomer);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Get dealerDealerCustomer by ID", description = "Retrieve a dealerDealerCustomer by their ID")
    public ResponseEntity<DealerCustomerResponseDTO> getDealerCustomerById(@PathVariable Long id) {
        DealerCustomerResponseDTO dealerDealerCustomer = dealerCustomerService.getDealerCustomerById(id);
        return ResponseEntity.ok(dealerDealerCustomer);
    }

    @GetMapping("/vendor/{dealerId}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Get dealerDealerCustomers by dealer", description = "Get all dealerDealerCustomers for a specific dealer with optional pagination")
    public ResponseEntity<Page<DealerCustomerResponseDTO>> getDealerCustomersByDealer(
            @PathVariable Long dealerId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerCustomerResponseDTO> dealerDealerCustomers = dealerCustomerService.getDealerCustomersByDealer(dealerId, pageable);
        return ResponseEntity.ok(dealerDealerCustomers);
    }

    @GetMapping("/my-dealer")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN')")
    @Operation(summary = "Get my dealer's dealerDealerCustomers", description = "Get all dealerDealerCustomers for the current user's dealer with optional pagination")
    public ResponseEntity<Page<DealerCustomerResponseDTO>> getMyDealerDealerCustomers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerCustomerResponseDTO> dealerDealerCustomers = dealerCustomerService.getMyDealerDealerCustomers(pageable);
        return ResponseEntity.ok(dealerDealerCustomers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Update dealerDealerCustomer", description = "Update an existing dealerDealerCustomer's information")
    public ResponseEntity<DealerCustomerResponseDTO> updateDealerCustomer(
            @PathVariable Long id,
            @Valid @RequestBody DealerCustomerRequestDTO requestDTO) {
        DealerCustomerResponseDTO dealerDealerCustomer = dealerCustomerService.updateDealerCustomer(id, requestDTO);
        return ResponseEntity.ok(dealerDealerCustomer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Delete dealerDealerCustomer", description = "Delete a dealerDealerCustomer and all associated data")
    public ResponseEntity<Void> deleteDealerCustomer(@PathVariable Long id) {
        dealerCustomerService.deleteDealerCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dealer/{dealerId}/search")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Search dealerDealerCustomers by dealer", description = "Search dealerDealerCustomers within a dealer using various criteria with optional pagination")
    public ResponseEntity<Page<DealerCustomerResponseDTO>> searchDealerCustomersByDealer(
            @PathVariable Long dealerId,
            @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerCustomerResponseDTO> dealerDealerCustomers = dealerCustomerService.searchDealerCustomersByDealer(dealerId, searchTerm, pageable);
        return ResponseEntity.ok(dealerDealerCustomers);
    }
}
