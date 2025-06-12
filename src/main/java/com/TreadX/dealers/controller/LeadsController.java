package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.LeadsRequestDTO;
import com.TreadX.dealers.dto.LeadsResponseDTO;
import com.TreadX.dealers.service.ConversionService;
import com.TreadX.dealers.service.LeadsService;
import com.TreadX.user.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadsController {

    private final LeadsService leadsService;
    private final ConversionService conversionService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<List<LeadsResponseDTO>> getAllLeads() {
        List<LeadsResponseDTO> leads = leadsService.getAllLeads();
        return new ResponseEntity<>(leads, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    public ResponseEntity<LeadsResponseDTO> getLeadById(@PathVariable("id") Long id) {
        authorizationService.checkLeadAccess(id, "READ");
        LeadsResponseDTO lead = leadsService.getLeadById(id);
        return new ResponseEntity<>(lead, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<LeadsResponseDTO> createLead(@RequestBody LeadsRequestDTO lead) {
        LeadsResponseDTO createdLead = leadsService.createLead(lead);
        return new ResponseEntity<>(createdLead, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    public ResponseEntity<LeadsResponseDTO> updateLead(@PathVariable("id") Long id, @RequestBody LeadsRequestDTO leadDetails) {
        authorizationService.checkLeadAccess(id, "UPDATE");
        LeadsResponseDTO updatedLead = leadsService.updateLead(id, leadDetails);
        return new ResponseEntity<>(updatedLead, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isLeadOwner(#id)")
    public ResponseEntity<Void> deleteLead(@PathVariable("id") Long id) {
        authorizationService.checkLeadAccess(id, "DELETE");
        leadsService.deleteLead(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/convert-to-contact")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<DealerContactResponseDTO> convertToContact(
            @PathVariable("id") Long id,
            @RequestBody DealerContactRequestDTO request) {
        authorizationService.checkContactConversionAccess();
        DealerContactResponseDTO contact = conversionService.convertLeadToContact(id, request);
        return new ResponseEntity<>(contact, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/convert-to-dealer")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<?> convertToDealer(
            @PathVariable("id") Long id,
            @RequestBody DealerContactRequestDTO request) {
        authorizationService.checkDealerConversionAccess();
        // TODO: Implement dealer conversion
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
} 