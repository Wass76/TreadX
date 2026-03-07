package com.TreadX.district.dealer.DealerCustomer.service;

import com.TreadX.district.dealer.DealerCustomer.dto.DealerCustomerRequestDTO;
import com.TreadX.district.dealer.DealerCustomer.dto.DealerCustomerResponseDTO;
import com.TreadX.district.dealer.DealerCustomer.entity.DealerCustomer;
import com.TreadX.district.dealer.DealerCustomer.mapper.DealerCustomerMapper;
import com.TreadX.district.dealer.DealerCustomer.repository.DealerCustomerRepository;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.repository.DealerRepository;
import com.TreadX.user.service.DealerContextService;
import com.TreadX.utils.DealerCustomerIdGenerator;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealerCustomerService {

    private final DealerCustomerRepository dealerCustomerRepository;
    private final DealerRepository dealerRepository;
    private final DealerCustomerMapper dealerCustomerMapper;
    private final DealerContextService dealerContextService;

    @Transactional
    public DealerCustomerResponseDTO createDealerCustomer(DealerCustomerRequestDTO requestDTO) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.info("Creating dealerCustomer for dealer: {}", dealerId);

        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with ID: " + dealerId));

        if (existsDuplicateDealerCustomer(requestDTO, dealer.getId())) {
            throw new ConflictException("DealerCustomer with same name, address, and phone already exists");
        }

        DealerCustomer dealerCustomer = dealerCustomerMapper.toEntity(requestDTO);
        dealerCustomer.setDealer(dealer);

        DealerCustomer savedDealerCustomer = dealerCustomerRepository.save(dealerCustomer);

        String dealerUniqueId = dealer.getDealerUniqueId();
        if (dealerUniqueId == null) {
            throw new IllegalStateException("Dealer does not have a unique ID set");
        }
        savedDealerCustomer.setDealerCustomerUniqueId(DealerCustomerIdGenerator.generateDealerCustomerUniqueId(dealerUniqueId, savedDealerCustomer.getId()));
        savedDealerCustomer = dealerCustomerRepository.save(savedDealerCustomer);

        log.info("DealerCustomer created successfully with ID: {} and unique ID: {}",
            savedDealerCustomer.getId(), savedDealerCustomer.getDealerCustomerUniqueId());
        return dealerCustomerMapper.toResponse(savedDealerCustomer);
    }

    @Transactional(readOnly = true)
    public DealerCustomerResponseDTO getDealerCustomerById(Long dealerDealerCustomerId) {
        DealerCustomer dealerCustomer = dealerCustomerRepository.findById(dealerDealerCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("DealerCustomer not found with ID: " + dealerDealerCustomerId));

        return dealerCustomerMapper.toResponse(dealerCustomer);
    }

    @Transactional(readOnly = true)
    public Page<DealerCustomerResponseDTO> getDealerCustomersByDealer(Long dealerId, Pageable pageable) {
        Page<DealerCustomer> dealerDealerCustomers = dealerCustomerRepository.findByDealerId(dealerId, pageable);

        return dealerDealerCustomers.map(dealerCustomerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DealerCustomerResponseDTO> getMyDealerDealerCustomers(Pageable pageable) {
        Long dealerId = dealerContextService.getCurrentDealerId();
        log.debug("Getting dealerDealerCustomers for current dealer: {}", dealerId);
        return getDealerCustomersByDealer(dealerId, pageable);
    }

    public DealerCustomerResponseDTO updateDealerCustomer(Long dealerDealerCustomerId, DealerCustomerRequestDTO requestDTO) {
        DealerCustomer dealerCustomer = dealerCustomerRepository.findById(dealerDealerCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("DealerCustomer not found with ID: " + dealerDealerCustomerId));

        dealerCustomerMapper.updateEntity(dealerCustomer, requestDTO);

        DealerCustomer updatedDealerCustomer = dealerCustomerRepository.save(dealerCustomer);

        return dealerCustomerMapper.toResponse(updatedDealerCustomer);
    }

    public void deleteDealerCustomer(Long dealerDealerCustomerId) {
        DealerCustomer dealerCustomer = dealerCustomerRepository.findById(dealerDealerCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("DealerCustomer not found with ID: " + dealerDealerCustomerId));

        dealerCustomerRepository.delete(dealerCustomer);
        log.info("DealerCustomer deleted successfully with ID: {}", dealerDealerCustomerId);
    }

    @Transactional(readOnly = true)
    public Page<DealerCustomerResponseDTO> searchDealerCustomersByDealer(Long dealerId, String searchTerm, Pageable pageable) {
        Page<DealerCustomer> dealerDealerCustomers = dealerCustomerRepository.searchByDealerAndTerm(dealerId, searchTerm, pageable);

        return dealerDealerCustomers.map(dealerCustomerMapper::toResponse);
    }

    private boolean existsDuplicateDealerCustomer(DealerCustomerRequestDTO requestDTO, Long dealerId) {
        return dealerCustomerRepository.existsDuplicateDealerCustomer(
                requestDTO.getFirstName(),
                requestDTO.getLastName(),
                requestDTO.getAddress().getStreetNumber(),
                requestDTO.getAddress().getPostalCode(),
                requestDTO.getPhoneNumber()
        );
    }
}
