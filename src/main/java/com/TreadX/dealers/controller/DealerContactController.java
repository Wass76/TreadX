package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.service.ConversionService;
import com.TreadX.dealers.service.DealerContactService;
import com.TreadX.user.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class DealerContactController {

    private final DealerContactService dealerContactService;
    private final AuthorizationService authorizationService;
    @Autowired
    private ConversionService conversionService;

    @GetMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<List<DealerContactResponseDTO>> getAllContacts() {
        return ResponseEntity.ok(dealerContactService.getAllDealerContacts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isContactOwner(#id)")
    public ResponseEntity<DealerContactResponseDTO> getContact(@PathVariable Long id) {
        return ResponseEntity.ok(dealerContactService.getDealerContactById(id));
    }

    @GetMapping("/dealer/{dealerId}")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<List<DealerContactResponseDTO>> getDealerContactsByDealer(@PathVariable Long dealerId) {
        return ResponseEntity.ok(dealerContactService.getDealerContactsByDealer(dealerId));
    }

    @PostMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<DealerContactResponseDTO> createContact(@RequestBody DealerContactRequestDTO contactDTO) {
        return ResponseEntity.ok(dealerContactService.createDealerContact(contactDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isContactOwner(#id)")
    public ResponseEntity<DealerContactResponseDTO> updateContact(
            @PathVariable Long id,
            @RequestBody DealerContactRequestDTO contactDTO) {
        return ResponseEntity.ok(dealerContactService.updateDealerContact(id, contactDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or @authz.isContactOwner(#id)")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        dealerContactService.deleteDealerContact(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/convert-to-dealer")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
    public ResponseEntity<Void> convertToDealer(
            @PathVariable Long id,
            @RequestBody DealerRequestDTO request) {
        authorizationService.checkDealerConversionAccess();
        conversionService.convertContactToDealer(id, request);
        return ResponseEntity.ok().build();
    }
} 