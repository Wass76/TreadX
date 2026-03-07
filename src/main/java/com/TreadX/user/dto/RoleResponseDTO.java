package com.TreadX.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {
    private Long id;
    private String name;
    private String description;
    private boolean isActive;
    private boolean isSystem;
    private boolean isSystemGenerated;
} 