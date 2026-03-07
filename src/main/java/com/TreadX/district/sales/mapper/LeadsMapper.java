package com.TreadX.district.sales.mapper;

import com.TreadX.address.mapper.AddressMapper;
import com.TreadX.address.service.AddressService;
import com.TreadX.district.dealer.dto.InitiateContactRequestDTO;
import com.TreadX.district.dealer.enums.LeadStatus;
import com.TreadX.district.sales.dto.LeadsRequestDTO;
import com.TreadX.district.sales.dto.LeadsResponseDTO;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.entity.LeadsHistory;
import com.TreadX.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadsMapper {
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final AddressService addressService;

    public Leads toEntity(LeadsRequestDTO request) {
        return Leads.builder()
                .businessName(request.getBusinessName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress() != null ? addressService.createOrReturnAddress(request.getAddress()) : null)
                .source(request.getSource())
                .sourceUrl(request.getSourceUrl())
                .uploadedFile(request.getUploadedFile())
                .status(request.getStatus() == null ? LeadStatus.PENDING : request.getStatus())
                .notes(request.getNotes())
                .flag(false) // Will be set by service
                .build();
    }

    public LeadsResponseDTO toResponse(Leads leads) {
        LeadsHistory currentHistory = leads.getCurrentHistory();
        // Format address with default values
        String city = "London";
        String province = "Ontario";
        String country = "Canada";
        
        // Build formatted address
        StringBuilder formattedAddress = new StringBuilder();
        if (leads.getAddress() != null) {
            formattedAddress.append(leads.getAddress().getStreetNumber()).append(" ");
            formattedAddress.append(leads.getAddress().getStreetName());
            formattedAddress.append(", ").append(leads.getAddress().getUnitNumber());
            formattedAddress.append(", ").append(leads.getAddress().getPostalCode());
        }
        formattedAddress.append(", ").append(city).append(", ").append(province).append(", ").append(country);
        
        return LeadsResponseDTO.builder()
                .id(leads.getId())
                .businessName(leads.getBusinessName())
                .phoneNumber(leads.getPhoneNumber())
                .address(addressMapper.toResponseDTO(leads.getAddress()))
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
                .dealerId(leads.getDealer() != null ? leads.getDealer().getId() : null)
                .dealerUniqueId(leads.getDealer() != null ? leads.getDealerUniqueId() : null)
                .createdAt(leads.getCreatedAt())
                .updatedAt(leads.getUpdatedAt())
                .addedBy(leads.getCreatedBy())
                .addedByName(getAddedByName(leads.getCreatedBy()))
                .lastModifiedBy(leads.getLastModifiedBy())
                .validatedBy(currentHistory != null && currentHistory.getValidatedBy() != null ? currentHistory.getValidatedBy().getId() : null)
                .validatedByFirstName(currentHistory != null && currentHistory.getValidatedBy() != null ? currentHistory.getValidatedBy().getFirstName() : null)
                .validatedByLastName(currentHistory != null && currentHistory.getValidatedBy() != null ? currentHistory.getValidatedBy().getLastName() : null)
                .validatedAt(currentHistory != null ? currentHistory.getValidatedAt() : null)
                .contactMethod(leads.getContactMethod())
                .contactMethodDetails(leads.getContactMethodDetails())
                .extensionNumber(leads.getExtensionNumber())
                .contactName(leads.getContactName())
                .position(leads.getPosition())
                .flag(leads.getFlag())
                .addedByManager(currentHistory != null && currentHistory.getAddedByManager() != null ? currentHistory.getAddedByManager() : false)
                .assignedTo(currentHistory != null && currentHistory.getAssignedTo() != null ? currentHistory.getAssignedTo().getId() : null)
                .assignedToFirstName(currentHistory != null && currentHistory.getAssignedTo() != null ? currentHistory.getAssignedTo().getFirstName() : null)
                .assignedToLastName(currentHistory != null && currentHistory.getAssignedTo() != null ? currentHistory.getAssignedTo().getLastName() : null)
                .assignedAt(currentHistory != null ? currentHistory.getAssignedAt() : null)
                .build();
    }

    public void updateEntityFromRequest(Leads leads, LeadsRequestDTO request) {
        if (request.getBusinessName() != null) {
            leads.setBusinessName(request.getBusinessName());
        }
        if (request.getPhoneNumber() != null) {
            leads.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            leads.setAddress(addressService.createOrReturnAddress(request.getAddress()));
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
        if (request.getAddress() != null) {
            leads.setAddress(addressService.createOrReturnAddress(request.getAddress()));
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