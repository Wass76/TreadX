package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.service.ConversionService;
import com.TreadX.dealers.service.DealerContactService;
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
@RequestMapping("/api/v1/contacts")
@Tag(name = "Contacts", description = "Contacts management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DealerContactController {

    private final DealerContactService dealerContactService;
    private final ConversionService conversionService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get all contacts",
        description = "Retrieves a paginated list of all contacts in the system. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved contacts",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<DealerContactResponseDTO>> getAllContacts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerContactResponseDTO> contacts = dealerContactService.getAllContacts(pageable);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or @authz.isContactOwner(#id)")
    @Operation(
        summary = "Get contact by ID",
        description = "Retrieves a specific contact by its ID. Requires PLATFORM_ADMIN, SALES_MANAGER role or ownership of the contact."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the contact",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerContactResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contact not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerContactResponseDTO> getContactById(
            @Parameter(description = "ID of the contact", required = true) @PathVariable("id") Long id) {
        if (!authorizationService.hasAccessToDealerContact(id, "READ")) {
            throw new AccessDeniedException("You don't have permission to read this contact");
        }
        DealerContactResponseDTO contact = dealerContactService.getContactById(id);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @GetMapping("/dealer/{dealerId}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get contacts by dealer",
        description = "Retrieves a paginated list of contacts for a specific dealer. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved contacts for the dealer",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "404", description = "Dealer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<DealerContactResponseDTO>> getContactsByDealer(
            @Parameter(description = "ID of the dealer", required = true) @PathVariable("dealerId") Long dealerId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DealerContactResponseDTO> contacts = dealerContactService.getContactsByDealer(dealerId, pageable);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Create new contact",
        description = "Creates a new contact in the system. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the contact",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerContactResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerContactResponseDTO> createContact(
            @Parameter(description = "Contact data", required = true) @RequestBody DealerContactRequestDTO contact) {
        DealerContactResponseDTO createdContact = dealerContactService.createContact(contact);
        return new ResponseEntity<>(createdContact, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or @authz.isContactOwner(#id)")
    @Operation(
        summary = "Update contact",
        description = "Updates an existing contact. Requires PLATFORM_ADMIN, SALES_MANAGER role or ownership of the contact."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the contact",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerContactResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contact not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerContactResponseDTO> updateContact(
            @Parameter(description = "ID of the contact", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Updated contact data", required = true) @RequestBody DealerContactRequestDTO contactDetails) {
        if (!authorizationService.hasAccessToDealerContact(id, "UPDATE")) {
            throw new AccessDeniedException("You don't have permission to update this contact");
        }
        DealerContactResponseDTO updatedContact = dealerContactService.updateContact(id, contactDetails);
        return new ResponseEntity<>(updatedContact, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or @authz.isContactOwner(#id)")
    @Operation(
        summary = "Delete contact",
        description = "Deletes a contact from the system. Requires PLATFORM_ADMIN, SALES_MANAGER role or ownership of the contact."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the contact"),
        @ApiResponse(responseCode = "404", description = "Contact not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteContact(
            @Parameter(description = "ID of the contact", required = true) @PathVariable("id") Long id) {
        if (!authorizationService.hasAccessToDealerContact(id, "DELETE")) {
            throw new AccessDeniedException("You don't have permission to delete this contact");
        }
        dealerContactService.deleteContact(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/convert-to-dealer")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<Void> convertToDealer(
            @PathVariable Long id,
            @RequestBody DealerRequestDTO request) {
        if (!authorizationService.hasAccessToDealerContact(id, "UPDATE")) {
            throw new AccessDeniedException("You don't have permission to convert this contact");
        }
        conversionService.convertContactToDealer(id, request);
        return ResponseEntity.ok().build();
    }
} 