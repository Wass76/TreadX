package com.TreadX.district.vendors.controller;

import com.TreadX.district.vendors.dto.VendorRequestDTO;
import com.TreadX.district.vendors.dto.VendorResponseDTO;
import com.TreadX.district.vendors.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorResponseDTO> createVendor(@RequestBody VendorRequestDTO request) {
        VendorResponseDTO vendor = vendorService.createVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vendor);
    }
} 