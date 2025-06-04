package com.TreadX.dealers.service;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.entity.DealerContact;
import com.TreadX.dealers.mapper.DealerContactMapper;
import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.address.service.AddressService;
import lombok.RequiredArgsConstructor;
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
    private final AddressService addressService;
    private final UserRepository userRepository;

    @Transactional
    public DealerContactResponseDTO createDealerContact(DealerContactRequestDTO request) {
        // Create address if provided
        Address address = null;
        if (request.getAddress() != null) {
            address = addressService.createAddress(request.getAddress());
        }

        // Create dealer contact with address
        DealerContact dealerContact = dealerContactMapper.toEntity(request);
        dealerContact.setAddress(address);

        // Set dealer if provided
        if (request.getBusiness() != null) {
            Dealer dealer = dealerRepository.findById(request.getBusiness())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getBusiness()));
            dealerContact.setBusiness(dealer);
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
    public DealerContactResponseDTO updateDealerContact(Long id, DealerContactRequestDTO request) {
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
            Address address = addressService.createAddress(request.getAddress());
            dealerContact.setAddress(address);
        }

        dealerContactMapper.updateEntityFromRequest(dealerContact, request);
        dealerContact = dealerContactRepository.save(dealerContact);
        return dealerContactMapper.toResponse(dealerContact);
    }

    public DealerContactResponseDTO getDealerContactById(Long id) {
        DealerContact dealerContact = dealerContactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer Contact not found with id: " + id));
        return dealerContactMapper.toResponse(dealerContact);
    }

    public List<DealerContactResponseDTO> getAllDealerContacts() {
        return dealerContactRepository.findAll().stream()
                .map(dealerContactMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<DealerContactResponseDTO> getDealerContactsByDealer(Long dealerId) {
        return dealerContactRepository.findByBusinessId(dealerId).stream()
                .map(dealerContactMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDealerContact(Long id) {
        if (!dealerContactRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dealer Contact not found with id: " + id);
        }
        dealerContactRepository.deleteById(id);
    }
} 