package com.TreadX.dealers.service;

import com.TreadX.address.entity.Address;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import com.TreadX.dealers.dto.LeadsRequestDTO;
import com.TreadX.dealers.dto.LeadsResponseDTO;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.entity.Leads;
import com.TreadX.dealers.enums.LeadStatus;
import com.TreadX.dealers.mapper.LeadsMapper;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.user.service.GeographicalAuthorizationService;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.utils.exception.InvalidStatusTransitionException;
import com.TreadX.utils.exception.UnAuthorizedException;
import com.TreadX.address.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LeadsService {

    @Autowired
    private LeadsRepository leadsRepository;
    @Autowired
    private DealerRepository dealerRepository;
    @Autowired
    private LeadsMapper leadsMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private GeographicalAuthorizationService geographicalAuthService;

    // Define valid status transitions
    private final Map<LeadStatus, Set<LeadStatus>> validTransitions = Map.of(
        LeadStatus.NEW, EnumSet.of(LeadStatus.CONTACTED, LeadStatus.CLOSED),
        LeadStatus.CONTACTED, EnumSet.of(LeadStatus.QUALIFIED, LeadStatus.CLOSED),
        LeadStatus.QUALIFIED, EnumSet.of(LeadStatus.CONVERTED, LeadStatus.CLOSED),
        LeadStatus.CONVERTED, Set.of(), // Terminal state
        LeadStatus.CLOSED, Set.of()     // Terminal state
    );

    /**
     * Validates if a status transition is allowed
     * @param currentStatus Current status of the lead
     * @param newStatus New status to transition to
     * @throws InvalidStatusTransitionException if the transition is not allowed
     */
    private void validateStatusTransition(LeadStatus currentStatus, LeadStatus newStatus) {
        Set<LeadStatus> allowedTransitions = validTransitions.get(currentStatus);
        
        if (allowedTransitions == null || allowedTransitions.isEmpty() || !allowedTransitions.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
    }

    @Transactional
    public LeadsResponseDTO createLead(LeadsRequestDTO request) {
        if(leadsRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new ConflictException("Phone number already exists");
        }
        if (leadsRepository.existsByBusinessEmail(request.getBusinessEmail())){
            throw new ConflictException("Business email already exists");
        }
        
        // Create address if provided
        Address address = null;
        if (request.getAddress() != null) {
            address = addressService.createOrReturnAddress(request.getAddress());
            
            // Validate geographical access
            if (!geographicalAuthService.hasAccessToLocation(
                    address.getCity(), address.getProvince(), address.getCountry())) {
                throw new UnAuthorizedException("No access to this geographical area");
            }
        }

        // Create lead with address
        Leads leads = leadsMapper.toEntity(request);
        leads.setAddress(address);

        // Set dealer if provided
        if (request.getDealerId() != null) {
            Dealer dealer = dealerRepository.findById(request.getDealerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getDealerId()));
            leads.setDealer(dealer);
        }

        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO updateLead(Long id, LeadsRequestDTO request) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        // Ownership and geographical access check
        Long currentUserId = geographicalAuthService.getCurrentUserId();
        boolean isOwner = leads.getCreatedBy() != null && leads.getCreatedBy().equals(currentUserId);
        boolean hasGeoAccess = false;
        if (leads.getAddress() != null) {
            hasGeoAccess = geographicalAuthService.hasAccessToLocation(
                leads.getAddress().getCity(),
                leads.getAddress().getProvince(),
                leads.getAddress().getCountry()
            );
        }
        if (!isOwner && !hasGeoAccess) {
            throw new UnAuthorizedException("You do not have permission to update this lead.");
        }

        // Validate status transition if status is being updated
        if (request.getStatus() != null && !request.getStatus().equals(leads.getStatus())) {
            validateStatusTransition(leads.getStatus(), request.getStatus());
        }

        // Update address if provided
        if (request.getAddress() != null) {
            Address address = addressService.createOrReturnAddress(request.getAddress());
            // Validate geographical access for new address
            if (!geographicalAuthService.hasAccessToLocation(
                    address.getCity(), address.getProvince(), address.getCountry())) {
                throw new UnAuthorizedException("No access to this geographical area");
            }
            leads.setAddress(address);
        }

        leadsMapper.updateEntityFromRequest(leads, request);
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    public LeadsResponseDTO getLeadById(Long id) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        return leadsMapper.toResponse(leads);
    }

    public Page<LeadsResponseDTO> getAllLeads(Pageable pageable) {
        // Apply geographical filtering based on user's territories
        List<Long> accessibleCityIds = geographicalAuthService.getAccessibleCityIds();
        
        if (accessibleCityIds.isEmpty()) {
            // Platform admin or no filtering needed
            return leadsRepository.findAll(pageable)
                    .map(leadsMapper::toResponse);
        } else {
            // Apply geographical filtering
            if (geographicalAuthService.canOnlyAccessOwnLeads()) {
                // Sales Agent: Only own leads in accessible cities
                return leadsRepository.findByCreatedByAndAddressCityIdIn(
                        geographicalAuthService.getCurrentUserId(),
                        accessibleCityIds, 
                        pageable)
                        .map(leadsMapper::toResponse);
            } else {
                // Sales Manager: All leads in accessible cities
                return leadsRepository.findByAddressCityIdIn(accessibleCityIds, pageable)
                        .map(leadsMapper::toResponse);
            }
        }
    }

    public List<LeadsResponseDTO> getLeadsByDealer(Long dealerId) {
        // Apply geographical filtering for dealer leads
        List<Long> accessibleCityIds = geographicalAuthService.getAccessibleCityIds();
        
        if (accessibleCityIds.isEmpty()) {
            // Platform admin or no filtering needed
            return leadsRepository.findByDealerId(dealerId).stream()
                    .map(leadsMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            // Apply geographical filtering
            if (geographicalAuthService.canOnlyAccessOwnLeads()) {
                // Sales Agent: Only own leads in accessible cities
                return leadsRepository.findByDealerIdAndCreatedByAndAddressCityIdIn(
                        dealerId,
                        geographicalAuthService.getCurrentUserId(),
                        accessibleCityIds)
                        .stream()
                        .map(leadsMapper::toResponse)
                        .collect(Collectors.toList());
            } else {
                // Sales Manager: All leads in accessible cities
                return leadsRepository.findByDealerIdAndAddressCityIdIn(dealerId, accessibleCityIds)
                        .stream()
                        .map(leadsMapper::toResponse)
                        .collect(Collectors.toList());
            }
        }
    }

    @Transactional
    public void deleteLead(Long id) {
        if (!leadsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lead not found with id: " + id);
        }
        leadsRepository.deleteById(id);
    }
} 