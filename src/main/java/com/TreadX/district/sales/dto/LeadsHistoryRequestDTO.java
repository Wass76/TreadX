package com.TreadX.district.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadsHistoryRequestDTO {

    @NotNull(message = "Lead ID is required")
    private Long leadId;

    private Long validatedById;
    private LocalDateTime validatedAt;
    private Boolean addedByManager;
    private Long assignedToId;
    private LocalDateTime assignedAt;
}
