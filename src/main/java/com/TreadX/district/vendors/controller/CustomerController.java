package com.TreadX.district.vendors.controller;

import com.TreadX.district.vendors.dto.CustomerRequestDTO;
import com.TreadX.district.vendors.dto.CustomerResponseDTO;
import com.TreadX.district.vendors.service.CustomerService;
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

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers in the vendor portal")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping("/")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Create a new customer", description = "Create a new customer for a vendor")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO customer = customerService.createCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by their ID")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    @GetMapping("/vendor/{vendorId}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Get customers by vendor", description = "Get all customers for a specific vendor with optional pagination")
    public ResponseEntity<Page<CustomerResponseDTO>> getCustomersByVendor(
            @PathVariable Long vendorId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<CustomerResponseDTO> customers = customerService.getCustomersByVendor(vendorId, pageable);
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/my-vendor")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN')")
    @Operation(summary = "Get my vendor's customers", description = "Get all customers for the current user's vendor with optional pagination")
    public ResponseEntity<Page<CustomerResponseDTO>> getMyVendorCustomers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<CustomerResponseDTO> customers = customerService.getMyVendorCustomers(pageable);
        return ResponseEntity.ok(customers);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Update customer", description = "Update an existing customer's information")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO customer = customerService.updateCustomer(id, requestDTO);
        return ResponseEntity.ok(customer);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Delete customer", description = "Delete a customer and all associated data")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/vendor/{vendorId}/search")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN', 'PLATFORM_ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Search customers by vendor", description = "Search customers within a vendor using various criteria with optional pagination")
    public ResponseEntity<Page<CustomerResponseDTO>> searchCustomersByVendor(
            @PathVariable Long vendorId,
            @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<CustomerResponseDTO> customers = customerService.searchCustomersByVendor(vendorId, searchTerm, pageable);
        return ResponseEntity.ok(customers);
    }
}