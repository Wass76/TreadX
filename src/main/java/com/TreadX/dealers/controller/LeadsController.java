package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.LeadsRequestDTO;
import com.TreadX.dealers.dto.LeadsResponseDTO;
import com.TreadX.dealers.service.ConversionService;
import com.TreadX.dealers.service.LeadsService;
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
@RequestMapping("/api/v1/leads")
@Tag(name = "Leads", description = "Leads management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LeadsController {

    private final LeadsService leadsService;
    private final ConversionService conversionService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get all leads",
        description = "Retrieves a paginated list of all leads in the system. Requires SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved leads",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<LeadsResponseDTO>> getAllLeads(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<LeadsResponseDTO> leads = leadsService.getAllLeads(pageable);
        return new ResponseEntity<>(leads, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    @Operation(
        summary = "Get lead by ID",
        description = "Retrieves a specific lead by its ID. Requires SALES_MANAGER role or ownership of the lead."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the lead",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LeadsResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LeadsResponseDTO> getLeadById(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id) {
        if (!authorizationService.hasAccessToLead(id, "READ")) {
            throw new AccessDeniedException("You don't have permission to read this lead");
        }
        LeadsResponseDTO lead = leadsService.getLeadById(id);
        return new ResponseEntity<>(lead, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Create new lead",
        description = "Creates a new lead in the system. Requires SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the lead",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LeadsResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LeadsResponseDTO> createLead(
            @Parameter(description = "Lead data", required = true) @RequestBody LeadsRequestDTO lead) {
        LeadsResponseDTO createdLead = leadsService.createLead(lead);
        return new ResponseEntity<>(createdLead, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    @Operation(
        summary = "Update lead",
        description = "Updates an existing lead. Requires SALES_MANAGER role or ownership of the lead."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the lead",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LeadsResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LeadsResponseDTO> updateLead(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Updated lead data", required = true) @RequestBody LeadsRequestDTO leadDetails) {
        if (!authorizationService.hasAccessToLead(id, "UPDATE")) {
            throw new AccessDeniedException("You don't have permission to update this lead");
        }
        LeadsResponseDTO updatedLead = leadsService.updateLead(id, leadDetails);
        return new ResponseEntity<>(updatedLead, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    @Operation(
        summary = "Delete lead",
        description = "Deletes a lead from the system. Requires SALES_MANAGER role or ownership of the lead."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the lead"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteLead(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id) {
        if (!authorizationService.hasAccessToLead(id, "DELETE")) {
            throw new AccessDeniedException("You don't have permission to delete this lead");
        }
        leadsService.deleteLead(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/convert-to-contact")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Convert lead to contact",
        description = "Converts a lead to a contact. Requires SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully converted lead to contact",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = DealerContactResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Invalid conversion data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DealerContactResponseDTO> convertToContact(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Contact data", required = true) @RequestBody DealerContactRequestDTO request) {
        if (!authorizationService.hasContactConversionAccess()) {
            throw new AccessDeniedException("You don't have permission to convert leads to contacts");
        }
        DealerContactResponseDTO contact = conversionService.convertLeadToContact(id, request);
        return new ResponseEntity<>(contact, HttpStatus.CREATED);
    }

//    @PostMapping("/{id}/convert-to-dealer")
//    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
//    @Operation(
//        summary = "Convert lead to dealer",
//        description = "Converts a lead to a dealer. Requires SALES_MANAGER or SALES_AGENT role."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "201", description = "Successfully converted lead to dealer"),
//        @ApiResponse(responseCode = "404", description = "Lead not found"),
//        @ApiResponse(responseCode = "400", description = "Invalid conversion data"),
//        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<?> convertToDealer(
//            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id,
//            @Parameter(description = "Dealer data", required = true) @RequestBody DealerContactRequestDTO request) {
//        authorizationService.checkDealerConversionAccess();
//        // TODO: Implement dealer conversion
//        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
//    }
} 