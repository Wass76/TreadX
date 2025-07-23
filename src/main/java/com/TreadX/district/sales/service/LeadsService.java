package com.TreadX.district.sales.service;

import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.district.sales.dto.LeadsRequestDTO;
import com.TreadX.district.sales.dto.LeadsResponseDTO;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.mapper.LeadsMapper;
import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.district.vendors.enums.LeadStatus;
import com.TreadX.user.entity.Territory;
import com.TreadX.user.service.AuthorizationService;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.TreadX.district.vendors.enums.LeadStatus.PENDING;
import com.TreadX.district.sales.dto.LeadValidationRequest;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.district.vendors.dto.InitiateContactRequestDTO;
import com.TreadX.district.sales.service.FileService;
import com.TreadX.user.service.UserTerritoryService;
import org.springframework.security.access.AccessDeniedException;
import com.TreadX.config.TerritoryContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.TreadX.user.service.TerritoryService;
import com.TreadX.user.repository.TerritoryRepository;

@Service
public class LeadsService {
    private static final Logger log = LoggerFactory.getLogger(LeadsService.class);
    @Autowired
    private LeadsRepository leadsRepository;
    @Autowired
    private LeadsMapper leadsMapper;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserTerritoryService userTerritoryService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private TerritoryRepository territoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public LeadsResponseDTO createLead(LeadsRequestDTO request, MultipartFile file) {
        if (leadsRepository.existsDuplicateLead(request.getBusinessName(), request.getStreetNumber(), request.getPostalCode(), request.getPhoneNumber())) {
            throw new ConflictException("A lead with the same business name, street number, postal code, and phone number already exists.");
        }
        Leads leads = leadsMapper.toEntity(request);
        // Handle file upload using FileService
        if (file != null && !file.isEmpty()) {
            fileService.validateFileUpload(file);
            String filePath = fileService.saveLeadFile(file);
            leads.setUploadedFile(filePath);
        }
        if (request.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(request.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getVendorId()));
            leads.setVendor(vendor);
        }
        leads.setStatus(PENDING);
        leads = leadsRepository.save(leads);
        // Log the current DB name
        try {
            String dbName = (String) entityManager.createNativeQuery("SELECT current_database()").getSingleResult();
            log.info("[LeadsService] Lead created in database: {}", dbName);
        } catch (Exception e) {
            log.warn("[LeadsService] Could not determine current database: {}", e.getMessage());
        }
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO updateLead(Long id, LeadsRequestDTO request, MultipartFile file) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        // Status transition validation
        if (request.getStatus() != null && !request.getStatus().equals(leads.getStatus())) {
            validateStatusTransition(leads.getStatus(), request.getStatus());
            // Set validation tracking fields if status is being approved or denied
            if (request.getStatus() == LeadStatus.APPROVED || request.getStatus() == LeadStatus.DENIED) {
                // You may want to get the current user from the security context or session
                // For now, set to null or implement user fetching logic
                leads.setValidatedBy(authorizationService.getCurrentUser());
                leads.setValidatedAt(java.time.LocalDateTime.now());
            }
        }
        leadsMapper.updateEntityFromRequest(leads, request);
        // Handle file upload using FileService
        if (file != null && !file.isEmpty()) {
            // Delete old file if exists
            if (leads.getUploadedFile() != null) {
                fileService.deleteLeadFile(leads.getUploadedFile());
            }
            fileService.validateFileUpload(file);
            String filePath = fileService.saveLeadFile(file);
            leads.setUploadedFile(filePath);
        }
        if (request.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(request.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getVendorId()));
            leads.setVendor(vendor);
        }
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    /**
     * Partially updates a lead with only the fields that are present in the request.
     * Fields that are null in the request will not be updated.
     */
    @Transactional
    public LeadsResponseDTO updateLeadPartial(Long id, LeadsRequestDTO request, MultipartFile file) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        // Status transition validation (only if status is being updated)
        if (request.getStatus() != null && !request.getStatus().equals(leads.getStatus())) {
            validateStatusTransition(leads.getStatus(), request.getStatus());
            // Set validation tracking fields if status is being approved or denied
            if (request.getStatus() == LeadStatus.APPROVED || request.getStatus() == LeadStatus.DENIED) {
                leads.setValidatedBy(authorizationService.getCurrentUser());
                leads.setValidatedAt(java.time.LocalDateTime.now());
            }
        }
        
        // Use partial update mapper
        leadsMapper.updateEntityFromRequestPartial(leads, request);
        
        // Handle file upload using FileService (only if file is provided)
        if (file != null && !file.isEmpty()) {
            // Delete old file if exists
            if (leads.getUploadedFile() != null) {
                fileService.deleteLeadFile(leads.getUploadedFile());
            }
            fileService.validateFileUpload(file);
            String filePath = fileService.saveLeadFile(file);
            leads.setUploadedFile(filePath);
        }
        
        // Handle dealer/vendor relationship (only if dealerId is provided)
        if (request.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(request.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getVendorId()));
            leads.setVendor(vendor);
        }
        
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    public LeadsResponseDTO getLeadById(Long id) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        return leadsMapper.toResponse(leads);
    }

    public Page<LeadsResponseDTO> getAllLeads(Pageable pageable) {
        return leadsRepository.findAll(pageable)
                .map(leadsMapper::toResponse);
    }

    public List<LeadsResponseDTO> getLeadsByDealer(Long dealerId) {
        return leadsRepository.findAll().stream()
                .filter(lead -> lead.getVendor() != null && lead.getVendor().getId().equals(dealerId))
                .map(leadsMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<LeadsResponseDTO> getLeadsByStatus(LeadStatus status, Pageable pageable) {
        return leadsRepository.findByStatus(status, pageable)
                .map(leadsMapper::toResponse);
    }

    @Transactional
    public void deleteLead(Long id) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        // Delete associated file
        if (leads.getUploadedFile() != null) {
            fileService.deleteLeadFile(leads.getUploadedFile());
        }
        
        leadsRepository.deleteById(id);
    }

    @Transactional
    public LeadsResponseDTO validateLead(Long id, LeadValidationRequest request) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        validateStatusTransition(leads.getStatus(), request.getStatus());
        leads.setStatus(request.getStatus());
        leads.setNotes(request.getNotes());
        User user = authorizationService.getCurrentUser();
        if (user != null) {
            leads.setValidatedBy(user);
        }
        leads.setValidatedAt(java.time.LocalDateTime.now());
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO initiateContact(Long id, InitiateContactRequestDTO request) {
        Leads lead = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        if (lead.getStatus() != LeadStatus.APPROVED) {
            throw new ConflictException("Lead must be in APPROVED status to initiate contact.");
        }
        validateStatusTransition(lead.getStatus(), LeadStatus.CONTACTED);
        lead.setStatus(LeadStatus.CONTACTED);
        // Use the new mapper method to update contact details
        leadsMapper.updateContactDetails(lead, request);
        lead = leadsRepository.save(lead);
        return leadsMapper.toResponse(lead);
    }

    private void validateStatusTransition(LeadStatus currentStatus, LeadStatus newStatus) {
        if (currentStatus == LeadStatus.PENDING && (newStatus == LeadStatus.APPROVED || newStatus == LeadStatus.DENIED || newStatus == LeadStatus.CONTACTED)) return;
        if (currentStatus == LeadStatus.APPROVED && newStatus == LeadStatus.CONTACTED) return;
        if (currentStatus == LeadStatus.CONTACTED && (newStatus == LeadStatus.ONBOARDED || newStatus == LeadStatus.DONE)) return;
        if (currentStatus == LeadStatus.ONBOARDED && newStatus == LeadStatus.DONE) return;
        if (currentStatus == LeadStatus.DENIED && newStatus == LeadStatus.PENDING) return; // allow re-validation
        throw new ConflictException("Invalid status transition from " + currentStatus + " to " + newStatus);
    }

    /**
     * Get leads from current user's accessible territories (automatic)
     */
    public List<LeadsResponseDTO> getMyLeads(Long userId) {
        List<Territory> accessibleTerritories = userTerritoryService.getAllAccessibleTerritories(userId);
        if (accessibleTerritories.isEmpty()) {
            throw new SecurityException("User has no territory access");
        }
        // For single territory users, return leads from that territory
        if (accessibleTerritories.size() == 1) {
            return getLeadsByTerritory(accessibleTerritories.get(0).getId());
        }
        // For multi-territory users, aggregate leads from all territories using dynamic DataSource context
        return accessibleTerritories.stream()
                .flatMap(territory -> getLeadsByTerritory(territory.getId()).stream())
                .collect(Collectors.toList());
    }

    /**
     * Get leads from specific territory (explicit)
     * Implements dynamic DataSource context logic.
     */
    public List<LeadsResponseDTO> getLeadsByTerritory(Long territoryId) {
        if (!userTerritoryService.hasAccessToTerritory(null, territoryId)) {
            throw new AccessDeniedException("Cannot access territory: " + territoryId);
        }
        // Set the territory context for dynamic DataSource routing
        String territoryCode = null;
        try {
            // Get the territory code for the given ID
            Territory territory = territoryService.getTerritoryById(territoryId);
            territoryCode = territory.getCode();
            TerritoryContextHolder.setTerritoryCode(territoryCode);
            // Query leads for this territory (now routed to correct DB)
            return getAllLeads(Pageable.unpaged()).getContent();
        } finally {
            // Always clear the context after the query
            TerritoryContextHolder.clear();
        }
    }

    /**
     * Get leads from specific territory with pagination (explicit)
     */
    public Page<LeadsResponseDTO> getLeadsByTerritory(Long territoryId, Pageable pageable) {
        // Check if user can access this territory
        if (!userTerritoryService.hasAccessToTerritory(null, territoryId)) {
            throw new AccessDeniedException("Cannot access territory: " + territoryId);
        }
        // TODO: Implement district database connection logic
        // For now, return leads from current database
        return getAllLeads(pageable);
    }

    /**
     * Get leads by status from current user's accessible territories (automatic)
     */
    public List<LeadsResponseDTO> getMyLeadsByStatus(Long userId, LeadStatus status) {
        List<com.TreadX.user.entity.Territory> accessibleTerritories = userTerritoryService.getAllAccessibleTerritories(userId);
        if (accessibleTerritories.isEmpty()) {
            throw new SecurityException("User has no territory access");
        }
        // For single territory users, return leads from that territory
        if (accessibleTerritories.size() == 1) {
            return getLeadsByTerritoryAndStatus(accessibleTerritories.get(0).getId(), status);
        }
        // For multi-territory users, return combined leads from all territories
        return accessibleTerritories.stream()
                .flatMap(territory -> getLeadsByTerritoryAndStatus(territory.getId(), status).stream())
                .collect(Collectors.toList());
    }

    /**
     * Get leads by status from specific territory (explicit)
     */
    public List<LeadsResponseDTO> getLeadsByTerritoryAndStatus(Long territoryId, LeadStatus status) {
        // Check if user can access this territory
        if (!userTerritoryService.hasAccessToTerritory(null, territoryId)) {
            throw new AccessDeniedException("Cannot access territory: " + territoryId);
        }
        // TODO: Implement district database connection logic
        // For now, return leads from current database
        return getLeadsByStatus(status, Pageable.unpaged()).getContent();
    }

    /**
     * Get lead by ID from current user's accessible territories (automatic)
     */
    public LeadsResponseDTO getMyLeadById(Long id) {
        // TODO: Implement district database connection logic
        // For now, return lead from current database
        return getLeadById(id);
    }

    /**
     * Get lead by ID from specific territory (explicit)
     */
    public LeadsResponseDTO getLeadByTerritoryAndId(Long territoryId, Long id) {
        // Check if user can access this territory
        if (!userTerritoryService.hasAccessToTerritory(null, territoryId)) {
            throw new AccessDeniedException("Cannot access territory: " + territoryId);
        }
        // TODO: Implement district database connection logic
        // For now, return lead from current database
        return getLeadById(id);
    }
} 