package com.TreadX.district.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadsHistoryResponseDTO {

    private Long id;
    private Long leadId;
    private Long validatedById;
    private String validatedByFirstName;
    private String validatedByLastName;
    private LocalDateTime validatedAt;
    private Boolean addedByManager;
    private Long assignedToId;
    private String assignedToFirstName;
    private String assignedToLastName;
    private LocalDateTime assignedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
