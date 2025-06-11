package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.LeadsRequestDTO;
import com.TreadX.dealers.dto.LeadsResponseDTO;
import com.TreadX.dealers.service.ConversionService;
import com.TreadX.dealers.service.LeadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadsController {

    @Autowired
    private LeadsService leadsService;
    @Autowired
    private ConversionService conversionService;

    @GetMapping
    public ResponseEntity<?> getAllLeads() {
        List<LeadsResponseDTO> leads = leadsService.getAllLeads();
        return new ResponseEntity<>(leads, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable("id") Long id) {
        Optional<LeadsResponseDTO> lead = Optional.ofNullable(leadsService.getLeadById(id));
        return lead.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createLead(@RequestBody LeadsRequestDTO lead) {
        LeadsResponseDTO createdLead = leadsService.createLead(lead);
        return new ResponseEntity<>(createdLead, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable("id") Long id, @RequestBody LeadsRequestDTO leadDetails) {
        LeadsResponseDTO updatedLead = leadsService.updateLead(id, leadDetails);
        return new ResponseEntity<>(updatedLead, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteLead(@PathVariable("id") Long id) {
        leadsService.deleteLead(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/convert-to-contact")
    @ResponseStatus(HttpStatus.CREATED)
    public DealerContactResponseDTO convertToContact(
            @PathVariable Long id,
            @RequestBody DealerContactRequestDTO request) {
        return conversionService.convertLeadToContact(id, request);
    }
} 