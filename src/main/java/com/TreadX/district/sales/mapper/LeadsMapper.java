package com.TreadX.district.sales.mapper;

import com.TreadX.district.sales.dto.LeadsRequestDTO;
import com.TreadX.district.sales.dto.LeadsResponseDTO;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.dealers.enums.LeadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadsMapper {
    public Leads toEntity(LeadsRequestDTO request) {
        return Leads.builder()
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .phoneNumber(request.getPhoneNumber())
                .streetNumber(request.getStreetNumber())
                .streetName(request.getStreetName())
                .aptUnitBldg(request.getAptUnitBldg())
                .postalCode(request.getPostalCode())
                .source(request.getSource())
                .sourceUrl(request.getSourceUrl())
                .uploadedFile(request.getUploadedFile())
                .status(request.getStatus() == null ? LeadStatus.NEW : request.getStatus())
                .notes(request.getNotes())
                .build();
    }

    public LeadsResponseDTO toResponse(Leads leads) {
        return LeadsResponseDTO.builder()
                .id(leads.getId())
                .businessName(leads.getBusinessName())
                .businessEmail(leads.getBusinessEmail())
                .phoneNumber(leads.getPhoneNumber())
                .streetNumber(leads.getStreetNumber())
                .streetName(leads.getStreetName())
                .aptUnitBldg(leads.getAptUnitBldg())
                .postalCode(leads.getPostalCode())
                .source(leads.getSource())
                .sourceUrl(leads.getSourceUrl())
                .uploadedFile(leads.getUploadedFile())
                .status(leads.getStatus())
                .notes(leads.getNotes())
                .dealerId(leads.getDealer() != null ? leads.getDealer().getId() : null)
                .dealerUniqueId(leads.getDealer() != null ? leads.getDealer().getDealerUniqueId() : null)
                .createdAt(leads.getCreatedAt())
                .updatedAt(leads.getUpdatedAt())
                .addedBy(leads.getCreatedBy())
                .lastModifiedBy(leads.getLastModifiedBy())
                .build();
    }

    public void updateEntityFromRequest(Leads leads, LeadsRequestDTO request) {
        if (request.getBusinessName() != null) {
            leads.setBusinessName(request.getBusinessName());
        }
        if (request.getBusinessEmail() != null) {
            leads.setBusinessEmail(request.getBusinessEmail());
        }
        if (request.getPhoneNumber() != null) {
            leads.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getStreetNumber() != null) {
            leads.setStreetNumber(request.getStreetNumber());
        }
        if (request.getStreetName() != null) {
            leads.setStreetName(request.getStreetName());
        }
        if (request.getAptUnitBldg() != null) {
            leads.setAptUnitBldg(request.getAptUnitBldg());
        }
        if (request.getPostalCode() != null) {
            leads.setPostalCode(request.getPostalCode());
        }
        if (request.getSource() != null) {
            leads.setSource(request.getSource());
        }
        if (request.getSourceUrl() != null) {
            leads.setSourceUrl(request.getSourceUrl());
        }
        if (request.getUploadedFile() != null) {
            leads.setUploadedFile(request.getUploadedFile());
        }
        if (request.getStatus() != null) {
            leads.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            leads.setNotes(request.getNotes());
        }
    }
} 