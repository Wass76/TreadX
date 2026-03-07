package com.TreadX.file.controller;

import com.TreadX.file.dto.FileRequestDTO;
import com.TreadX.file.dto.FileResponseDTO;
import com.TreadX.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "Central file store APIs")
public class FileController {

    private final FileService fileService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create file record", description = "Create a new file record in the central file store")
    public ResponseEntity<FileResponseDTO> create(@Valid @RequestBody FileRequestDTO requestDTO) {
        FileResponseDTO created = fileService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get file by ID", description = "Retrieve a file record by its ID")
    public ResponseEntity<FileResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete file record", description = "Delete a file record by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
