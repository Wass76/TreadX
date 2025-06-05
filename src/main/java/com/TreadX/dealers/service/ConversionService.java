package com.TreadX.dealers.service;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.entity.Address;
import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.entity.DealerContact;
import com.TreadX.dealers.entity.Leads;
import com.TreadX.dealers.enums.ContactStatus;
import com.TreadX.dealers.enums.LeadStatus;
import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversionService {
    private final DealerContactService dealerContactService;
    private final DealerService dealerService;
    private final LeadsRepository leadsRepository;
    private final DealerContactRepository dealerContactRepository;
    private final DealerRepository dealerRepository;

    @Transactional
    public DealerContactResponseDTO convertLeadToContact(Long leadId, DealerContactRequestDTO request) {
        // Get the lead
        Leads lead = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));

        // Copy relevant data from lead to contact request
        if (request.getBusinessEmail() == null) {
            request.setBusinessEmail(lead.getBusinessEmail());
        }
        if (request.getBusinessPhone() == null) {
            request.setBusinessPhone(lead.getPhoneNumber());
        }
        if (request.getBusinessName() == null) {
            request.setBusinessName(lead.getBusinessName());
        }
        if (request.getAddress() == null && lead.getAddress() != null) {
            // Reuse the lead's address
            Address address = lead.getAddress();
            request.setAddress(AddressRequestDTO.builder()
                    .streetName(address.getStreetName())
                    .streetNumber(address.getStreetNumber())
                    .cityId(address.getCity().getId())
                    .countryId(address.getCountry().getId())
                    .unitNumber(address.getUnitNumber())
                    .stateId(address.getProvince().getId())
                    .postalCode(address.getPostalCode())
                    .specialInstructions(address.getSpecialInstructions())
                    .build());
        }

        // Set the lead ID in the request
        request.setConvertedFromLeadId(leadId);

        // Create the contact with OPENED status
        DealerContactResponseDTO contact = dealerContactService.createDealerContact(request);

        // Update lead status to SENT
        lead.setStatus(LeadStatus.SENT);
        leadsRepository.save(lead);

        return contact;
    }

    @Transactional
    public DealerResponseDTO convertContactToDealer(Long contactId, DealerRequestDTO request) {
        // Get the contact
        DealerContact contact = dealerContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Copy relevant data from contact to dealer request
        if (request.getEmail() == null) {
            request.setEmail(contact.getBusinessEmail());
        }
        if (request.getPhone() == null) {
            request.setPhone(contact.getBusinessPhone());
        }
        if (request.getName() == null) {
            request.setName(contact.getBusinessName());
        }
        if (request.getAddress() == null && contact.getAddress() != null) {
            // Reuse the contact's address
            Address address = contact.getAddress();
            request.setAddress(AddressRequestDTO.builder()
                    .streetName(address.getStreetName())
                    .streetNumber(address.getStreetNumber())
                    .cityId(address.getCity().getId())
                    .countryId(address.getCountry().getId())
                    .unitNumber(address.getUnitNumber())
                    .stateId(address.getProvince().getId())
                    .postalCode(address.getPostalCode())
                    .specialInstructions(address.getSpecialInstructions())
                    .build());
        }

        // Create the dealer
        DealerResponseDTO dealer = dealerService.createDealer(request);

        // Update contact status to CLOSED and link to dealer
        contact.setStatus(ContactStatus.CLOSED);
        contact.setBusiness(dealerRepository.findById(dealer.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Dealer not found with id: " + dealer.getId())
        ));
        dealerContactRepository.save(contact);

        // If this contact was converted from a lead, update the lead's dealer
        if (contact.getConvertedFromLead() != null) {
            Leads lead = contact.getConvertedFromLead();
            lead.setDealer(dealerRepository.findById(dealer.getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Dealer not found with id: " + dealer.getId())
            ));
            leadsRepository.save(lead);
        }

        return dealer;
    }

    /**
     * Helper method to check if an address is already associated with a lead or contact
     */
    private boolean isAddressInUse(Address address) {
        return leadsRepository.existsByAddress(address) || 
               dealerContactRepository.existsByAddress(address);
    }
} 