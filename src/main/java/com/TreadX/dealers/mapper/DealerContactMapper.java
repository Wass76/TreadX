package com.TreadX.dealers.mapper;

import com.TreadX.address.entity.Address;
import com.TreadX.address.service.AddressService;
import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.entity.DealerContact;
import com.TreadX.dealers.enums.Channel;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealerContactMapper {

    private final AddressService addressService;
    private final UserRepository userRepository;

    public DealerContact toEntity(DealerContactRequestDTO request) {
        return DealerContact.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .businessPhone(request.getBusinessPhone())
                .source(LeadSource.valueOf(request.getSource()))
                .channel(Channel.valueOf(request.getChannel()))
                .position(request.getPosition())
                .ex(request.getEx())
                .notes(request.getNotes())
                .build();
    }

    public DealerContactResponseDTO toResponse(DealerContact dealerContact) {
        DealerContactResponseDTO response = DealerContactResponseDTO.builder()
                .id(dealerContact.getId())
                .firstName(dealerContact.getFirstName())
                .lastName(dealerContact.getLastName())
                .businessName(dealerContact.getBusinessName())
                .businessEmail(dealerContact.getBusinessEmail())
                .businessPhone(dealerContact.getBusinessPhone())
                .source(dealerContact.getSource().toString())
                .channel(dealerContact.getChannel())
                .position(dealerContact.getPosition())
                .ex(dealerContact.getEx())
                .notes(dealerContact.getNotes())
                .owner(dealerContact.getOwner())
                .createdAt(dealerContact.getCreatedAt())
                .updatedAt(dealerContact.getUpdatedAt())
                .createdBy(dealerContact.getCreatedBy())
                .lastModifiedBy(dealerContact.getLastModifiedBy())
                .build();

        // Add address information if available
        if (dealerContact.getAddress() != null) {
            response.setAddress(addressService.getAddressResponse(dealerContact.getAddress()));
        }
        return response;
    }

    public void updateEntityFromRequest(DealerContact dealerContact, DealerContactRequestDTO request) {
        if (request.getFirstName() != null) {
            dealerContact.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            dealerContact.setLastName(request.getLastName());
        }
        if (request.getBusinessName() != null) {
            dealerContact.setBusinessName(request.getBusinessName());
        }
        if (request.getBusinessEmail() != null) {
            dealerContact.setBusinessEmail(request.getBusinessEmail());
        }
        if (request.getBusinessPhone() != null) {
            dealerContact.setBusinessPhone(request.getBusinessPhone());
        }
        if (request.getSource() != null) {
            dealerContact.setSource(LeadSource.valueOf(request.getSource()));
        }
        if (request.getChannel() != null) {
            dealerContact.setChannel(Channel.valueOf(request.getChannel()));
        }
        if (request.getPosition() != null) {
            dealerContact.setPosition(request.getPosition());
        }
        if (request.getEx() != null) {
            dealerContact.setEx(request.getEx());
        }
        if (request.getOwner() != null) {
            dealerContact.setOwner(userRepository.findById(request.getOwner()).orElseThrow(
                    () -> new ResourceNotFoundException("Owner not found")
            ));
        }
        if (request.getNotes() != null) {
            dealerContact.setNotes(request.getNotes());
        }
    }
} 