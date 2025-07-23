package com.TreadX.district.sales.controller;

import com.TreadX.district.sales.dto.LeadsRequestDTO;
import com.TreadX.district.sales.dto.LeadsResponseDTO;
import com.TreadX.district.sales.dto.LeadValidationRequest;
import com.TreadX.district.sales.service.LeadsService;
import com.TreadX.user.service.AuthorizationService;
import com.TreadX.district.vendors.enums.LeadStatus;
import com.TreadX.district.vendors.dto.InitiateContactRequestDTO;
import com.TreadX.utils.exception.ResourceNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.TreadX.district.sales.service.FileService;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import com.TreadX.user.service.TerritoryService;
import com.TreadX.config.TerritoryContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.user.entity.Territory;
import com.TreadX.user.service.UserTerritoryService;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(name = "Leads", description = "Leads management APIs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LeadsController {

    private static final Logger log = LoggerFactory.getLogger(LeadsController.class);
    private final LeadsService leadsService;
//    private final ConversionService conversionService;
    private final AuthorizationService authorizationService;
    private final FileService fileService;
    private final TerritoryService territoryService;
    private final UserTerritoryService userTerritoryService;

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get all leads",
        description = "Retrieves a paginated list of all leads in the system. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
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

//    @GetMapping("/my-leads")
//    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
//    @Operation(
//        summary = "Get my leads (automatic territory resolution)",
//        description = "Retrieves leads from current user's accessible territories. System automatically determines user's territory access. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's leads",
//            content = @Content(mediaType = "application/json",
//            schema = @Schema(implementation = List.class))),
//        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<List<LeadsResponseDTO>> getMyLeads() {
//        Long userId = authorizationService.getCurrentUser().getId();
//        List<LeadsResponseDTO> leads = leadsService.getMyLeads(userId);
//        return new ResponseEntity<>(leads, HttpStatus.OK);
//    }

//    @GetMapping("/territories/{territoryCode}")
//    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
//    @Operation(
//        summary = "Get leads by territory (explicit territory access)",
//        description = "Retrieves leads from a specific territory. User must have access to the specified territory. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Successfully retrieved leads from territory",
//            content = @Content(mediaType = "application/json",
//            schema = @Schema(implementation = Page.class))),
//        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
//        @ApiResponse(responseCode = "404", description = "Territory not found"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<Page<LeadsResponseDTO>> getLeadsByTerritory(
//            @Parameter(description = "Territory code (e.g., N6B, N5V)", required = true) @PathVariable String territoryCode,
//            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
//            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
//            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
//        Long territoryId = territoryService.getTerritoryResponseByCode(territoryCode).getId();
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sortBy));
//        Page<LeadsResponseDTO> leads = leadsService.getLeadsByTerritory(territoryId, pageable);
//        return new ResponseEntity<>(leads, HttpStatus.OK);
//    }

//    @GetMapping("/my-leads/status")
//    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
//    @Operation(
//        summary = "Get my leads by status (automatic territory resolution)",
//        description = "Retrieves leads by status from current user's accessible territories. System automatically determines user's territory access. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's leads by status",
//            content = @Content(mediaType = "application/json",
//            schema = @Schema(implementation = List.class))),
//        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<List<LeadsResponseDTO>> getMyLeadsByStatus(
//            @Parameter(description = "Lead status to filter by", required = true) @RequestParam LeadStatus status) {
//        Long userId = authorizationService.getCurrentUser().getId();
//        List<LeadsResponseDTO> leads = leadsService.getMyLeadsByStatus(userId, status);
//        return new ResponseEntity<>(leads, HttpStatus.OK);
//    }

//    @GetMapping("/territories/{territoryCode}/status")
//    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
//    @Operation(
//        summary = "Get leads by territory and status (explicit territory access)",
//        description = "Retrieves leads by status from a specific territory. User must have access to the specified territory. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Successfully retrieved leads from territory by status",
//            content = @Content(mediaType = "application/json",
//            schema = @Schema(implementation = Page.class))),
//        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
//        @ApiResponse(responseCode = "404", description = "Territory not found"),
//        @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<Page<LeadsResponseDTO>> getLeadsByTerritoryAndStatus(
//            @Parameter(description = "Territory code (e.g., N6B, N5V)", required = true) @PathVariable String territoryCode,
//            @Parameter(description = "Lead status to filter by", required = true) @RequestParam LeadStatus status,
//            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
//            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
//            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
//        Long territoryId = territoryService.getTerritoryResponseByCode(territoryCode).getId();
//        List<LeadsResponseDTO> leads = leadsService.getLeadsByTerritoryAndStatus(territoryId, status);
//        // For consistency, wrap in a Page object
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sortBy));
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), leads.size());
//        Page<LeadsResponseDTO> pageResult = new PageImpl<>(leads.subList(start, end), pageable, leads.size());
//        return new ResponseEntity<>(pageResult, HttpStatus.OK);
//    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    @Operation(
        summary = "Get lead by ID",
        description = "Retrieves a specific lead by its ID. Requires PLATFORM_ADMIN, SALES_MANAGER role or ownership of the lead."
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

    @GetMapping("/status")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Get leads by status (paginated)",
        description = "Retrieves a paginated list of leads filtered by status. Requires PLATFORM_ADMIN, SALES_MANAGER, or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved leads by status",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status value"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<LeadsResponseDTO>> getLeadsByStatus(
            @RequestParam LeadStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<LeadsResponseDTO> leads = leadsService.getLeadsByStatus(status, pageable);
        return ResponseEntity.ok(leads);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Create new lead",
        description = "Creates a new lead in the system. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
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
            @Parameter(description = "Lead data", required = true) @RequestPart("lead") LeadsRequestDTO lead,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestHeader(value = "X-Territory-Code", required = false) String territoryCode) {
        LeadsResponseDTO createdLead = leadsService.createLead(lead, file);
        return new ResponseEntity<>(createdLead, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    @Operation(
        summary = "Update lead",
        description = "Updates an existing lead. Requires PLATFORM_ADMIN, SALES_MANAGER role or ownership of the lead."
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
            @Parameter(description = "Updated lead data", required = true) @RequestPart("lead") LeadsRequestDTO leadDetails,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        if (!authorizationService.hasAccessToLead(id, "UPDATE")) {
            throw new AccessDeniedException("You don't have permission to update this lead");
        }
        LeadsResponseDTO updatedLead = leadsService.updateLead(id, leadDetails, file);
        return new ResponseEntity<>(updatedLead, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    @Operation(
        summary = "Partially update lead",
        description = "Partially updates an existing lead. Only fields that are present in the request will be updated. Requires PLATFORM_ADMIN, SALES_MANAGER role or ownership of the lead."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully partially updated the lead",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LeadsResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LeadsResponseDTO> updateLeadPartial(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Partial lead data", required = true) @RequestPart("lead") LeadsRequestDTO leadDetails,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        if (!authorizationService.hasAccessToLead(id, "UPDATE")) {
            throw new AccessDeniedException("You don't have permission to update this lead");
        }
        LeadsResponseDTO updatedLead = leadsService.updateLeadPartial(id, leadDetails, file);
        return new ResponseEntity<>(updatedLead, HttpStatus.OK);
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
        summary = "Validate (approve/deny) a lead",
        description = "Validates a lead by approving or denying it. Requires PLATFORM_ADMIN or SALES_MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully validated the lead",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LeadsResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LeadsResponseDTO> validateLead(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id,
            @RequestBody LeadValidationRequest request) {
        if (!authorizationService.hasAccessToLead(id, "UPDATE")) {
            throw new AccessDeniedException("You don't have permission to validate this lead");
        }
        LeadsResponseDTO response = leadsService.validateLead(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/initiate-contact")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Initiate contact for a lead",
        description = "Initiates contact for a lead and updates its status to CONTACTED."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully initiated contact for the lead",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadsResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LeadsResponseDTO> initiateContact(
            @PathVariable Long id,
            @RequestBody InitiateContactRequestDTO request) {
        LeadsResponseDTO response = leadsService.initiateContact(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    @Operation(
        summary = "Delete lead",
        description = "Deletes a lead from the system. Requires PLATFORM_ADMIN, SALES_MANAGER role or ownership of the lead."
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

    @GetMapping("/{id}/file")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Download uploaded file for a lead",
        description = "Downloads the uploaded file associated with a lead. Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully downloaded the file"),
        @ApiResponse(responseCode = "404", description = "Lead not found or file not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Resource> downloadLeadFile(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id) {
        Resource resource = fileService.getFileForDownload(id);
        HttpHeaders headers = fileService.getDownloadHeaders(id);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/{id}/preview")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(
        summary = "Preview uploaded file for a lead",
        description = "Displays the uploaded file directly in browser (especially useful for images). Requires PLATFORM_ADMIN, SALES_MANAGER or SALES_AGENT role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully displayed the file"),
        @ApiResponse(responseCode = "404", description = "Lead not found or file not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Resource> previewLeadFile(
            @Parameter(description = "ID of the lead", required = true) @PathVariable("id") Long id) {
        Resource resource = fileService.getFileForPreview(id);
        MediaType contentType = fileService.getContentType(id);
        
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }

} 