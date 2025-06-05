package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.service.DealerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;

    @GetMapping
    public ResponseEntity<List<DealerResponseDTO>> getAllDealers() {
        List<DealerResponseDTO> dealers = dealerService.getAllDealers();
        return new ResponseEntity<>(dealers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealerResponseDTO> getDealerById(@PathVariable Long id) {
        DealerResponseDTO dealer = dealerService.getDealerById(id);
        return new ResponseEntity<>(dealer, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DealerResponseDTO> createDealer(@Valid @RequestBody DealerRequestDTO dealerRequestDTO) {
        DealerResponseDTO createdDealer = dealerService.createDealer(dealerRequestDTO);
        return new ResponseEntity<>(createdDealer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealerResponseDTO> updateDealer(
            @PathVariable Long id,
            @Valid @RequestBody DealerRequestDTO dealerRequestDTO) {
        DealerResponseDTO updatedDealer = dealerService.updateDealer(id, dealerRequestDTO);
        return new ResponseEntity<>(updatedDealer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealer(@PathVariable Long id) {
        dealerService.deleteDealer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 