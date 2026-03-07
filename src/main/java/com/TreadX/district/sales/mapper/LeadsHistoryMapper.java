package com.TreadX.district.sales.mapper;

import com.TreadX.district.sales.dto.LeadsHistoryRequestDTO;
import com.TreadX.district.sales.dto.LeadsHistoryResponseDTO;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.entity.LeadsHistory;
import com.TreadX.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LeadsHistoryMapper {

    public LeadsHistory toEntity(LeadsHistoryRequestDTO requestDTO, Leads lead, User validatedBy, User assignedTo) {
        if (requestDTO == null) {
            return null;
        }
        return LeadsHistory.builder()
                .lead(lead)
                .validatedBy(validatedBy)
                .validatedAt(requestDTO.getValidatedAt())
                .addedByManager(requestDTO.getAddedByManager() != null ? requestDTO.getAddedByManager() : false)
                .assignedTo(assignedTo)
                .assignedAt(requestDTO.getAssignedAt())
                .build();
    }

    public LeadsHistoryResponseDTO toResponseDTO(LeadsHistory entity) {
        if (entity == null) {
            return null;
        }
        return LeadsHistoryResponseDTO.builder()
                .id(entity.getId())
                .leadId(entity.getLead() != null ? entity.getLead().getId() : null)
                .validatedById(entity.getValidatedBy() != null ? entity.getValidatedBy().getId() : null)
                .validatedByFirstName(entity.getValidatedBy() != null ? entity.getValidatedBy().getFirstName() : null)
                .validatedByLastName(entity.getValidatedBy() != null ? entity.getValidatedBy().getLastName() : null)
                .validatedAt(entity.getValidatedAt())
                .addedByManager(entity.getAddedByManager())
                .assignedToId(entity.getAssignedTo() != null ? entity.getAssignedTo().getId() : null)
                .assignedToFirstName(entity.getAssignedTo() != null ? entity.getAssignedTo().getFirstName() : null)
                .assignedToLastName(entity.getAssignedTo() != null ? entity.getAssignedTo().getLastName() : null)
                .assignedAt(entity.getAssignedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
