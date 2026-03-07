package com.TreadX.district.sales.controller;

import com.TreadX.district.sales.dto.LeadsHistoryRequestDTO;
import com.TreadX.district.sales.dto.LeadsHistoryResponseDTO;
import com.TreadX.district.sales.service.LeadsHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads-history")
@RequiredArgsConstructor
@Tag(name = "Leads History", description = "Lead validation and assignment history APIs")
public class LeadsHistoryController {

    private final LeadsHistoryService leadsHistoryService;

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(summary = "Create leads history record", description = "Create a new history record for a lead (validation/assignment)")
    public ResponseEntity<LeadsHistoryResponseDTO> create(@Valid @RequestBody LeadsHistoryRequestDTO requestDTO) {
        LeadsHistoryResponseDTO created = leadsHistoryService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(summary = "Get leads history by ID", description = "Retrieve a leads history record by ID")
    public ResponseEntity<LeadsHistoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(leadsHistoryService.getById(id));
    }

    @GetMapping("/lead/{leadId}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    @Operation(summary = "Get history by lead ID", description = "Retrieve all history records for a lead, newest first")
    public ResponseEntity<List<LeadsHistoryResponseDTO>> getByLeadId(@PathVariable Long leadId) {
        return ResponseEntity.ok(leadsHistoryService.getByLeadId(leadId));
    }
}
