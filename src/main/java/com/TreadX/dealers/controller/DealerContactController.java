package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.service.ConversionService;
import com.TreadX.dealers.service.DealerContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dealer-contacts")
@RequiredArgsConstructor
public class DealerContactController {

    private final DealerContactService dealerContactService;
    private final ConversionService conversionService;

    @GetMapping
    public ResponseEntity<?> getAllDealerContacts() {
        List<DealerContactResponseDTO> dealerContacts = dealerContactService.getAllDealerContacts();
        return new ResponseEntity<>(dealerContacts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDealerContactById(@PathVariable("id") Long id) {
        Optional<DealerContactResponseDTO> dealerContact = Optional.ofNullable(dealerContactService.getDealerContactById(id));
        return dealerContact.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<?> getDealerContactsByDealer(@PathVariable("dealerId") Long dealerId) {
        List<DealerContactResponseDTO> dealerContacts = dealerContactService.getDealerContactsByDealer(dealerId);
        return new ResponseEntity<>(dealerContacts, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createDealerContact(@RequestBody DealerContactRequestDTO dealerContact) {
        DealerContactResponseDTO createdDealerContact = dealerContactService.createDealerContact(dealerContact);
        return new ResponseEntity<>(createdDealerContact, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDealerContact(@PathVariable("id") Long id, @RequestBody DealerContactRequestDTO dealerContactDetails) {
        DealerContactResponseDTO updatedDealerContact = dealerContactService.updateDealerContact(id, dealerContactDetails);
        return new ResponseEntity<>(updatedDealerContact, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDealerContact(@PathVariable("id") Long id) {
        dealerContactService.deleteDealerContact(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/convert-to-dealer")
    @ResponseStatus(HttpStatus.CREATED)
    public DealerResponseDTO convertToDealer(
            @PathVariable Long id,
            @RequestBody DealerRequestDTO request) {
        return conversionService.convertContactToDealer(id, request);
    }
} 