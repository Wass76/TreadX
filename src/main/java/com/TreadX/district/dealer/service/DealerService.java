package com.TreadX.district.dealer.service;

import com.TreadX.district.dealer.dto.DealerRequestDTO;
import com.TreadX.district.dealer.dto.DealerResponseDTO;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.enums.DealerStatus;
import com.TreadX.district.dealer.enums.LeadStatus;
import com.TreadX.district.dealer.mapper.DealerMapper;
import com.TreadX.district.dealer.repository.DealerRepository;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.user.config.RoleConstants;
import com.TreadX.utils.DealerIdGenerator;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealerService {
    private final DealerRepository dealerRepository;
    private final DealerMapper dealerMapper;
    private final LeadsRepository leadsRepository;

    public Page<DealerResponseDTO> getAllDealers(Pageable pageable) {
        return dealerRepository.findAll(pageable)
                .map(dealerMapper::toResponse);
    }

    public DealerResponseDTO getDealerById(Long id) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));
        return dealerMapper.toResponse(dealer);
    }

    @Transactional
    public DealerResponseDTO createDealer(DealerRequestDTO request) {
        // 1. Validate lead is CONTACTED
        Leads lead = leadsRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + request.getLeadId()));
        if (lead.getStatus() != LeadStatus.CONTACTED) {
            throw new ConflictException("Lead must be in CONTACTED status to create a dealer.");
        }
        
        // 2. Validate user access management data
        validateUserAccessManagement(request);
        
        // 3. Map info from lead and request to vendor
        Dealer dealer = dealerMapper.toEntity(request);
        
        // 4. Save vendor first to get the ID
        dealer = dealerRepository.save(dealer);
        
        // 5. Generate vendorUniqueId using the new format: 001010001 + vendorId
        dealer.setDealerUniqueId(DealerIdGenerator.generateDealerUniqueId(dealer.getId()));
        dealer = dealerRepository.save(dealer);
        
        // 6. Log user access management information
        logUserAccessManagement(dealer, request);
        
        // 7. Update lead status to ONBOARDED
        lead.setStatus(LeadStatus.ONBOARDED);
        leadsRepository.save(lead);
        
        // 8. Return VendorResponseDTO
        return dealerMapper.toResponse(dealer);
    }

    @Transactional
    public DealerResponseDTO updateDealer(Long id, DealerRequestDTO request) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));
        dealerMapper.updateEntityFromRequest(dealer, request);
        dealer = dealerRepository.save(dealer);
        return dealerMapper.toResponse(dealer);
    }

    /**
     * Partially updates a vendor with only the fields that are present in the request.
     * Fields that are null in the request will not be updated.
     */
    @Transactional
    public DealerResponseDTO updateDealerPartial(Long id, DealerRequestDTO request) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));
        dealerMapper.updateEntityFromRequestPartial(dealer, request);
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

    public Page<DealerResponseDTO> searchDealers(String query, Pageable pageable) {
        return dealerRepository.searchDealers(query, pageable)
                .map(dealerMapper::toResponse);
    }

    public Page<DealerResponseDTO> getDealersByStatus(DealerStatus status, Pageable pageable) {
        return dealerRepository.findByDealerStatus(status, pageable)
                .map(dealerMapper::toResponse);
    }
    
    /**
     * Validates user access management data
     */
    private void validateUserAccessManagement(DealerRequestDTO request) {
        if (request.getTotalUsers() != null && request.getTotalUsers() <= 0) {
            throw new IllegalArgumentException("Total users must be greater than 0");
        }
        
        if (request.getUserRoles() != null && !request.getUserRoles().isEmpty()) {
            // Validate that user roles are valid vendor roles
            for (Map.Entry<String, Integer> entry : request.getUserRoles().entrySet()) {
                String role = entry.getKey();
                Integer count = entry.getValue();
                
                if (!isValidDealerRole(role)) {
                    throw new IllegalArgumentException("Invalid dealer role: " + role);
                }
                
                if (count <= 0) {
                    throw new IllegalArgumentException("User count for role " + role + " must be greater than 0");
                }
            }
            
            // Validate total users matches sum of role counts
            if (request.getTotalUsers() != null) {
                int totalFromRoles = request.getUserRoles().values().stream()
                        .mapToInt(Integer::intValue)
                        .sum();
                
                if (totalFromRoles != request.getTotalUsers()) {
                    throw new IllegalArgumentException("Total users (" + request.getTotalUsers() + 
                            ") does not match sum of role counts (" + totalFromRoles + ")");
                }
            }
        }
    }
    
    /**
     * Checks if the role is a valid vendor role
     */
    private boolean isValidDealerRole(String role) {
        return RoleConstants.DEALER_ADMIN.equals(role) || 
               RoleConstants.DEALER_EMPLOYEE.equals(role) || 
               RoleConstants.DEALER_TECHNICIAN.equals(role);
    }
    
    /**
     * Logs user access management information for later integration
     */
    private void logUserAccessManagement(Dealer dealer, DealerRequestDTO request) {
        log.info("Dealer {} created with user access management:", dealer.getBusinessName());
        log.info("  - Total Users: {}", request.getTotalUsers());
        
        if (request.getUserRoles() != null) {
            log.info("  - User Role Distribution:");
            for (Map.Entry<String, Integer> entry : request.getUserRoles().entrySet()) {
                log.info("    * {}: {} users", entry.getKey(), entry.getValue());
            }
        }
        
        log.info("  - Note: User account creation will be handled by dealer application");
    }
} 