package com.TreadX.dealers.mapper;

import com.TreadX.address.mapper.AddressMapper;
import com.TreadX.address.service.AddressService;
import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.entity.DealerContact;
import com.TreadX.dealers.enums.Channel;
import com.TreadX.dealers.enums.ContactStatus;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealerContactMapper {

    private final AddressService addressService;
    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final AddressMapper addressMapper;
//    private final DealerMapper dealerMapper;

    public DealerContact toEntity(DealerContactRequestDTO request) {
        DealerContact dealerContact = DealerContact.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .businessPhone(request.getBusinessPhone())
                .source(request.getSource() == null ? null : request.getSource())
                .status(request.getStatus() == null ? ContactStatus.OPENED : request.getStatus())
                .channel(request.getChannel() == null ? null : request.getChannel())
                .position(request.getPosition())
                .ex(request.getEx())
                .notes(request.getNotes())
                .build();

        // Set dealer if provided
        if (request.getBusiness() != null) {
            Dealer dealer = dealerRepository.findById(request.getBusiness())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getBusiness()));
            dealerContact.setBusiness(dealer);
        }
        return dealerContact;
    }

    public DealerContactResponseDTO toResponse(DealerContact dealerContact) {
        DealerContactResponseDTO response = DealerContactResponseDTO.builder()
                .id(dealerContact.getId())
                .firstName(dealerContact.getFirstName())
                .lastName(dealerContact.getLastName())
                .businessName(dealerContact.getBusinessName())
                .businessEmail(dealerContact.getBusinessEmail())
                .businessPhone(dealerContact.getBusinessPhone())
                .source(dealerContact.getSource() == null ? null : dealerContact.getSource())
                .channel(dealerContact.getChannel() == null ? null : dealerContact.getChannel())
                .status(dealerContact.getStatus() == null ? null : dealerContact.getStatus())
                .position(dealerContact.getPosition())
                .ex(dealerContact.getEx())
                .notes(dealerContact.getNotes())
                .ownerId(dealerContact.getOwner().getId())
                .ownerName(dealerContact.getOwner().getFirstName()+" " + dealerContact.getOwner().getLastName())
                .createdAt(dealerContact.getCreatedAt())
                .updatedAt(dealerContact.getUpdatedAt())
                .createdBy(dealerContact.getCreatedBy())
                .lastModifiedBy(dealerContact.getLastModifiedBy())
                .build();

        // Set dealer if provided
        if(dealerContact.getBusiness() != null){
            response.setDealerId(dealerContact.getBusiness().getId());
            response.setDealerUniqueId(dealerContact.getBusiness().getDealerUniqueId());
        }

        // Add address information if available
        if (dealerContact.getAddress() != null) {
            response.setAddress(addressMapper.toResponseDTO(dealerContact.getAddress()));
        }

        // Add converted from lead ID if available
        if (dealerContact.getConvertedFromLead() != null) {
            response.setConvertedFromLeadId(dealerContact.getConvertedFromLead().getId());
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
            dealerContact.setSource(request.getSource());
        }
        if (request.getChannel() != null) {
            dealerContact.setChannel(request.getChannel());
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
        if(request.getStatus() != null){
            dealerContact.setStatus(request.getStatus());
        }
    }
} 