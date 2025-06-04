package com.TreadX.dealers.mapper;

import com.TreadX.address.entity.Address;
import com.TreadX.address.service.AddressService;
import com.TreadX.dealers.dto.LeadsRequestDTO;
import com.TreadX.dealers.dto.LeadsResponseDTO;
import com.TreadX.dealers.entity.Leads;
import com.TreadX.dealers.enums.LeadSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadsMapper {

    private final AddressService addressService;

    public Leads toEntity(LeadsRequestDTO request) {
        return Leads.builder()
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .phoneNumber(request.getPhoneNumber())
                .source(LeadSource.valueOf(request.getSource()))
                .status(request.getStatus())
                .notes(request.getNotes())
                .build();
    }

    public LeadsResponseDTO toResponse(Leads leads) {
        LeadsResponseDTO response = LeadsResponseDTO.builder()
                .id(leads.getId())
                .businessName(leads.getBusinessName())
                .businessEmail(leads.getBusinessEmail())
                .phoneNumber(leads.getPhoneNumber())
                .source(leads.getSource())
                .status(leads.getStatus())
                .notes(leads.getNotes())
                .dealerId(leads.getDealer() != null ? leads.getDealer().getId() : null)
                .createdAt(leads.getCreatedAt())
                .updatedAt(leads.getUpdatedAt())
                .addedBy(leads.getCreatedBy())
                .lastModifiedBy(leads.getLastModifiedBy())
                .build();

        // Add address information if available
        if (leads.getAddress() != null) {
            response.setAddress(addressService.getAddressResponse(leads.getAddress()));
        }

        return response;
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
        if (request.getSource() != null) {
            leads.setSource(LeadSource.valueOf(request.getSource()));
        }
        if (request.getStatus() != null) {
            leads.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            leads.setNotes(request.getNotes());
        }
    }
} 