package com.TreadX.dealers.service;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.entity.DealerContact;
import com.TreadX.dealers.entity.Leads;
import com.TreadX.dealers.mapper.DealerContactMapper;
import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealerContactService {

    private final DealerContactRepository dealerContactRepository;
    private final DealerRepository dealerRepository;
    private final DealerContactMapper dealerContactMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeadsRepository leadsRepository;

    @Transactional
    public DealerContactResponseDTO createContact(DealerContactRequestDTO request) {

        // Create address if provided
        Address address = null;
        // If this contact is converted from a lead, reuse the lead's address
        if (request.getConvertedFromLeadId() != null) {
            Leads lead = leadsRepository.findById(request.getConvertedFromLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + request.getConvertedFromLeadId()));
            address = lead.getAddress();
        } else {
            if (request.getAddress() != null) {
                address = addressService.createOrReturnAddress(request.getAddress());
            }
        }

        // Create dealer contact with address
        DealerContact dealerContact = dealerContactMapper.toEntity(request);
        dealerContact.setAddress(address);

        // Set the lead if this contact was converted from a lead
        if (request.getConvertedFromLeadId() != null) {
            Leads lead = leadsRepository.findById(request.getConvertedFromLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + request.getConvertedFromLeadId()));
            dealerContact.setConvertedFromLead(lead);
        }

        dealerContact = dealerContactRepository.save(dealerContact);
        DealerContact finalDealerContact = dealerContact;
        dealerContact.setOwner(userRepository.findById(dealerContact.getCreatedBy()).orElseThrow(
                ()-> new ResourceNotFoundException("User not fount with id: " + finalDealerContact.getCreatedBy())
        ));
        dealerContactRepository.save(dealerContact);
        return dealerContactMapper.toResponse(dealerContact);
    }

    @Transactional
    public DealerContactResponseDTO updateContact(Long id, DealerContactRequestDTO request) {
        DealerContact dealerContact = dealerContactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer Contact not found with id: " + id));

        // Check for email uniqueness if email is being changed
        if (request.getBusinessEmail() != null && !dealerContact.getBusinessEmail().equals(request.getBusinessEmail()) && 
            dealerContactRepository.existsByBusinessEmail(request.getBusinessEmail())) {
            throw new ConflictException("Business email already exists");
        }

        // Check for phone uniqueness if phone is being changed
        if (request.getBusinessPhone() != null && !dealerContact.getBusinessPhone().equals(request.getBusinessPhone()) && 
            dealerContactRepository.existsByBusinessPhone(request.getBusinessPhone())) {
            throw new ConflictException("Business phone already exists");
        }

        // Update address if provided
        if (request.getAddress() != null) {
            Address address = addressService.createOrReturnAddress(request.getAddress());
            dealerContact.setAddress(address);
        }

        dealerContactMapper.updateEntityFromRequest(dealerContact, request);
        dealerContact = dealerContactRepository.save(dealerContact);
        return dealerContactMapper.toResponse(dealerContact);
    }

    public List<DealerContactResponseDTO> getDealerContactsByDealer(Long dealerId) {
        return dealerContactRepository.findByBusinessId(dealerId).stream()
                .map(dealerContactMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<DealerContactResponseDTO> getAllContacts(Pageable pageable) {
        return dealerContactRepository.findAll(pageable)
                .map(dealerContactMapper::toResponse);
    }

    public DealerContactResponseDTO getContactById(Long id) {
        DealerContact contact = dealerContactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + id));
        return dealerContactMapper.toResponse(contact);
    }

    @Transactional
    public void deleteContact(Long id) {
        if (!dealerContactRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contact not found with id: " + id);
        }
        dealerContactRepository.deleteById(id);
    }

    public Page<DealerContactResponseDTO> getContactsByDealer(Long dealerId, Pageable pageable) {
        return dealerContactRepository.findByBusinessId(dealerId, pageable)
                .map(dealerContactMapper::toResponse);
    }
} 