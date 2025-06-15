package com.TreadX.tire.controller;

import com.TreadX.tire.dto.TireDTO;
import com.TreadX.tire.service.TireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/v1/tires")
@RequiredArgsConstructor
@Tag(name = "Tire Management", description = "APIs for managing tires")
@CrossOrigin("*")
public class TireController {
    private final TireService tireService;

//    @PostMapping
    @Operation(summary = "Create a new tire", description = "Creates a new tire with the provided details")
    public ResponseEntity<TireDTO> createTire(@RequestBody TireDTO tireDTO) {
        return ResponseEntity.ok(tireService.createTire(tireDTO));
    }

//    @GetMapping
    @Operation(summary = "Get all tires", description = "Retrieves a list of all tires")
    public ResponseEntity<List<TireDTO>> getAllTires() {
        return ResponseEntity.ok(tireService.getAllTires());
    }

//    @GetMapping("/{id}")
    @Operation(summary = "Get tire by ID", description = "Retrieves a tire by its ID")
    public ResponseEntity<TireDTO> getTireById(@PathVariable Long id) {
        return ResponseEntity.ok(tireService.getTireById(id));
    }

//    @PutMapping("/{id}")
    @Operation(summary = "Update tire", description = "Updates an existing tire with the provided details")
    public ResponseEntity<TireDTO> updateTire(@PathVariable Long id, @RequestBody TireDTO tireDTO) {
        return ResponseEntity.ok(tireService.updateTire(id, tireDTO));
    }

//    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tire", description = "Deletes a tire by its ID")
    public ResponseEntity<Void> deleteTire(@PathVariable Long id) {
        tireService.deleteTire(id);
        return ResponseEntity.ok().build();
    }
} 