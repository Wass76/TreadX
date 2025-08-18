package com.TreadX.district.sales.mapper;

import com.TreadX.district.sales.dto.LeadsRequestDTO;
import com.TreadX.district.sales.dto.LeadsResponseDTO;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.vendors.dto.InitiateContactRequestDTO;
import com.TreadX.district.vendors.enums.LeadStatus;
import com.TreadX.user.mapper.UserMapper;
import com.TreadX.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadsMapper {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Leads toEntity(LeadsRequestDTO request) {
        return Leads.builder()
                .businessName(request.getBusinessName())
                .phoneNumber(request.getPhoneNumber())
                .streetNumber(request.getStreetNumber())
                .streetName(request.getStreetName())
                .aptUnitBldg(request.getAptUnitBldg())
                .postalCode(request.getPostalCode())
                .source(request.getSource())
                .sourceUrl(request.getSourceUrl())
                .uploadedFile(request.getUploadedFile())
                .status(request.getStatus() == null ? LeadStatus.PENDING : request.getStatus())
                .notes(request.getNotes())
                .flag(false) // Will be set by service
                .addedByManager(false) // Will be set by service
                .build();
    }

    public LeadsResponseDTO toResponse(Leads leads) {
        // Format address with default values
        String city = "London";
        String province = "Ontario";
        String country = "Canada";
        
        // Build formatted address
        StringBuilder formattedAddress = new StringBuilder();
        if (leads.getStreetNumber() != null && !leads.getStreetNumber().trim().isEmpty()) {
            formattedAddress.append(leads.getStreetNumber()).append(" ");
        }
        if (leads.getStreetName() != null && !leads.getStreetName().trim().isEmpty()) {
            formattedAddress.append(leads.getStreetName());
        }
        if (leads.getAptUnitBldg() != null && !leads.getAptUnitBldg().trim().isEmpty()) {
            formattedAddress.append(", ").append(leads.getAptUnitBldg());
        }
        if (leads.getPostalCode() != null && !leads.getPostalCode().trim().isEmpty()) {
            formattedAddress.append(", ").append(leads.getPostalCode());
        }
        formattedAddress.append(", ").append(city).append(", ").append(province).append(", ").append(country);
        
        return LeadsResponseDTO.builder()
                .id(leads.getId())
                .businessName(leads.getBusinessName())
                .phoneNumber(leads.getPhoneNumber())
                .streetNumber(leads.getStreetNumber())
                .streetName(leads.getStreetName())
                .aptUnitBldg(leads.getAptUnitBldg())
                .postalCode(leads.getPostalCode())
                .city(city)
                .province(province)
                .country(country)
                .formattedAddress(formattedAddress.toString())
                .source(leads.getSource())
                .sourceUrl(leads.getSourceUrl())
                .uploadedFile(leads.getUploadedFile() != null ? "/api/v1/leads/" + leads.getId() + "/file" : null)
                .previewUrl(leads.getUploadedFile() != null ? "/api/v1/leads/" + leads.getId() + "/preview" : null)
                .status(leads.getStatus())
                .notes(leads.getNotes())
                .vendorId(leads.getVendor() != null ? leads.getVendor().getId() : null)
                .vendorUniqueId(leads.getVendor() != null ? leads.getVendorUniqueId() : null)
                .createdAt(leads.getCreatedAt())
                .updatedAt(leads.getUpdatedAt())
                .addedBy(leads.getCreatedBy())
                .addedByName(getAddedByName(leads.getCreatedBy()))
                .lastModifiedBy(leads.getLastModifiedBy())
                .validatedBy(leads.getValidatedBy() != null ? leads.getValidatedBy().getId() : null)
                .validatedByFirstName(leads.getValidatedBy()!= null ? leads.getValidatedBy().getFirstName() : null)
                .validatedByLastName(leads.getValidatedBy() != null ? leads.getValidatedBy().getLastName() : null)
                .validatedAt(leads.getValidatedAt())
                .contactMethod(leads.getContactMethod())
                .contactMethodDetails(leads.getContactMethodDetails())
                .extensionNumber(leads.getExtensionNumber())
                .contactName(leads.getContactName())
                .position(leads.getPosition())
                .flag(leads.getFlag())
                .addedByManager(leads.getAddedByManager())
                .assignedTo(leads.getAssignedTo() != null ? leads.getAssignedTo().getId() : null)
                .assignedToFirstName(leads.getAssignedTo() != null ? leads.getAssignedTo().getFirstName() : null)
                .assignedToLastName(leads.getAssignedTo() != null ? leads.getAssignedTo().getLastName() : null)
                .assignedAt(leads.getAssignedAt())
                .build();
    }

    public void updateEntityFromRequest(Leads leads, LeadsRequestDTO request) {
        if (request.getBusinessName() != null) {
            leads.setBusinessName(request.getBusinessName());
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

    public void updateContactDetails(Leads leads, InitiateContactRequestDTO request) {
        leads.setContactMethod(request.getContactMethod());
        leads.setContactMethodDetails(request.getContactMethodDetails());
        leads.setExtensionNumber(request.getExtensionNumber());
        leads.setContactName(request.getContactName());
        leads.setPosition(request.getPosition());
        // Update status to CONTACTED when contact is initiated
        leads.setStatus(LeadStatus.CONTACTED);
    }

    /**
     * Updates leads entity with only the fields that are present (not null) in the request.
     * This method is useful for partial updates where you don't want to overwrite existing data.
     */
    public void updateEntityFromRequestPartial(Leads leads, LeadsRequestDTO request) {
        if (request.getBusinessName() != null) {
            leads.setBusinessName(request.getBusinessName());
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
        if (request.getPhoneNumber() != null) {
            leads.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getSource() != null) {
            leads.setSource(request.getSource());
        }
        if (request.getSourceUrl() != null) {
            leads.setSourceUrl(request.getSourceUrl());
        }
        if (request.getNotes() != null) {
            leads.setNotes(request.getNotes());
        }
        if (request.getStatus() != null) {
            leads.setStatus(request.getStatus());
        }
//        if (request.getDealerId() != null) {
//            // Handle dealer/vendor relationship if needed
//            // This might need additional logic depending on your requirements
//        }
    }

    private String getAddedByName(Long userId) {
        if (userId == null) {
            return null;
        }
        
        return userRepository.findById(userId)
                .map(user -> {
                    String firstName = user.getFirstName() != null ? user.getFirstName() : "";
                    String lastName = user.getLastName() != null ? user.getLastName() : "";
                    return (firstName + " " + lastName).trim();
                })
                .orElse(null);
    }
} 