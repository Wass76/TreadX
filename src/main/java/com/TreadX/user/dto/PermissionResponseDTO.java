package com.TreadX.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String resource;
    private String action;
    private boolean isActive;
    private boolean isSystemGenerated;
} 