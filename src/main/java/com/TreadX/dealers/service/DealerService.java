package com.TreadX.dealers.service;

import com.TreadX.address.entity.Address;
import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.mapper.DealerMapper;
import com.TreadX.dealers.repository.DealerRepository;
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
public class DealerService {

    private final DealerRepository dealerRepository;
    private final DealerMapper dealerMapper;
    private final AddressService addressService;

    @Transactional
    public DealerResponseDTO createDealer(DealerRequestDTO request) {
        if (dealerRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (dealerRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Phone number already exists");
        }

        // Create address if provided
        Address address = null;
        if (request.getAddress() != null) {
            address = addressService.createOrReturnAddress(request.getAddress());
        }

        // Create dealer with address
        Dealer dealer = dealerMapper.toEntity(request);
        dealer.setAddress(address);

        // Save the dealer first to get the dealer ID
        Dealer savedDealer = dealerRepository.save(dealer);

        // Set dealerUniqueId based on cityUniqueId
        if (address != null && address.getCity() != null) {
            String dealerUniqueId = address.getCity().getCityUniqueId() + savedDealer.getId();
            savedDealer.setDealerUniqueId(dealerUniqueId);
            savedDealer = dealerRepository.save(savedDealer);
        }

        return dealerMapper.toResponse(savedDealer);
    }

    public List<DealerResponseDTO> getAllDealers() {
        return dealerRepository.findAll().stream()
                .map(dealerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public DealerResponseDTO getDealerById(Long id) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));
        return dealerMapper.toResponse(dealer);
    }

    @Transactional
    public DealerResponseDTO updateDealer(Long id, DealerRequestDTO request) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));

        // Check for email uniqueness if email is being changed
        if (!dealer.getEmail().equals(request.getEmail()) && 
            dealerRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        // Check for phone uniqueness if phone is being changed
        if (!dealer.getPhone().equals(request.getPhone()) && 
            dealerRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Phone number already exists");
        }

        // Update address if provided
        if (request.getAddress() != null) {
            Address address = addressService.createOrReturnAddress(request.getAddress());
            dealer.setAddress(address);
            
            // Update dealerUniqueId based on new cityUniqueId
            if (address.getCity() != null) {
                String dealerUniqueId = address.getCity().getCityUniqueId() + dealer.getId();
                dealer.setDealerUniqueId(dealerUniqueId);
            }
        }

        dealerMapper.updateEntityFromRequest(dealer, request);
        dealer = dealerRepository.save(dealer);
        return dealerMapper.toResponse(dealer);
    }

    @Transactional
    public void deleteDealer(Long id) {
        if (!dealerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dealer not found with id: " + id);
        }
        dealerRepository.deleteById(id);
    }
} 