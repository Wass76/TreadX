package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.service.DealerService;
import com.TreadX.user.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<DealerResponseDTO>> getAllDealers() {
        return ResponseEntity.ok(dealerService.getAllDealers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<DealerResponseDTO> getDealer(@PathVariable Long id) {
        return ResponseEntity.ok(dealerService.getDealerById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<DealerResponseDTO> createDealer(@RequestBody DealerRequestDTO dealerDTO) {
        authorizationService.checkDealerManagementAccess();
        return ResponseEntity.ok(dealerService.createDealer(dealerDTO, null));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<DealerResponseDTO> updateDealer(
            @PathVariable Long id,
            @RequestBody DealerRequestDTO dealerDTO) {
        authorizationService.checkDealerManagementAccess();
        return ResponseEntity.ok(dealerService.updateDealer(id, dealerDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Void> deleteDealer(@PathVariable Long id) {
        authorizationService.checkDealerManagementAccess();
        dealerService.deleteDealer(id);
        return ResponseEntity.ok().build();
    }
} 