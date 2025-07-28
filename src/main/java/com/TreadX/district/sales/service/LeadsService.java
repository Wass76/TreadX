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
        
        // Set flag based on duplicate check
        boolean hasFlag = checkForDuplicates(request.getBusinessName(), request.getPhoneNumber(), 
                                          request.getStreetNumber(), request.getStreetName(), request.getPostalCode());
        leads.setFlag(hasFlag);
        
        // Set addedByManager based on current user role
        User currentUser = authorizationService.getCurrentUser();
        boolean isManager = authorizationService.hasRole("SALES_MANAGER") || authorizationService.hasRole("PLATFORM_ADMIN");
        leads.setAddedByManager(isManager);
        
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
        }
        
        // Handle file upload
        if (file != null && !file.isEmpty()) {
            fileService.validateFileUpload(file);
            String filePath = fileService.saveLeadFile(file);
            leads.setUploadedFile(filePath);
        }
        
        // Update flag if business name, phone, or address changed
        if (request.getBusinessName() != null || request.getPhoneNumber() != null || 
            request.getStreetNumber() != null || request.getStreetName() != null || request.getPostalCode() != null) {
            String businessName = request.getBusinessName() != null ? request.getBusinessName() : leads.getBusinessName();
            String phoneNumber = request.getPhoneNumber() != null ? request.getPhoneNumber() : leads.getPhoneNumber();
            String streetNumber = request.getStreetNumber() != null ? request.getStreetNumber() : leads.getStreetNumber();
            String streetName = request.getStreetName() != null ? request.getStreetName() : leads.getStreetName();
            String postalCode = request.getPostalCode() != null ? request.getPostalCode() : leads.getPostalCode();
            
            boolean hasFlag = checkForDuplicates(businessName, phoneNumber, streetNumber, streetName, postalCode);
            leads.setFlag(hasFlag);
        }
        
        leadsMapper.updateEntityFromRequest(leads, request);
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO updateLeadPartial(Long id, LeadsRequestDTO request, MultipartFile file) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        // Handle file upload
        if (file != null && !file.isEmpty()) {
            fileService.validateFileUpload(file);
            String filePath = fileService.saveLeadFile(file);
            leads.setUploadedFile(filePath);
        }
        
        // Update flag if business name, phone, or address changed
        if (request.getBusinessName() != null || request.getPhoneNumber() != null || 
            request.getStreetNumber() != null || request.getStreetName() != null || request.getPostalCode() != null) {
            String businessName = request.getBusinessName() != null ? request.getBusinessName() : leads.getBusinessName();
            String phoneNumber = request.getPhoneNumber() != null ? request.getPhoneNumber() : leads.getPhoneNumber();
            String streetNumber = request.getStreetNumber() != null ? request.getStreetNumber() : leads.getStreetNumber();
            String streetName = request.getStreetName() != null ? request.getStreetName() : leads.getStreetName();
            String postalCode = request.getPostalCode() != null ? request.getPostalCode() : leads.getPostalCode();
            
            boolean hasFlag = checkForDuplicates(businessName, phoneNumber, streetNumber, streetName, postalCode);
            leads.setFlag(hasFlag);
        }
        
        leadsMapper.updateEntityFromRequestPartial(leads, request);
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    public LeadsResponseDTO getLeadById(Long id) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        return leadsMapper.toResponse(leads);
    }

    public Page<LeadsResponseDTO> getAllLeads(Pageable pageable) {
        // Only managers can see all leads
        if (!authorizationService.hasRole("SALES_MANAGER") && !authorizationService.hasRole("PLATFORM_ADMIN")) {
            throw new AccessDeniedException("Only managers can view all leads");
        }
        
        return leadsRepository.findAll(pageable).map(leadsMapper::toResponse);
    }

    public List<LeadsResponseDTO> getLeadsByDealer(Long dealerId) {
        return leadsRepository.findByVendorId(dealerId).stream()
                .map(leadsMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<LeadsResponseDTO> getLeadsByStatus(LeadStatus status, Pageable pageable) {
        // For agents, show their leads, unassigned manager leads, and leads assigned to them
        if (authorizationService.hasRole("SALES_AGENT")) {
            User currentUser = authorizationService.getCurrentUser();
            List<Leads> myLeads = leadsRepository.findMyLeadsByStatus(currentUser.getId(), status);
            List<Leads> unassignedManagerLeads = leadsRepository.findUnassignedManagerLeads();
            List<Leads> assignedToMeLeads = leadsRepository.findByAssignedToIdAndStatus(currentUser.getId(), status);
            
            // Combine and filter by status
            List<Leads> allAccessibleLeads = new java.util.ArrayList<>();
            allAccessibleLeads.addAll(myLeads);
            allAccessibleLeads.addAll(unassignedManagerLeads.stream()
                    .filter(lead -> lead.getStatus().equals(status))
                    .collect(Collectors.toList()));
            allAccessibleLeads.addAll(assignedToMeLeads);
            
            return new org.springframework.data.domain.PageImpl<>(
                allAccessibleLeads.stream().map(leadsMapper::toResponse).collect(Collectors.toList()),
                pageable,
                allAccessibleLeads.size()
            );
        }
        
        // For managers, show all leads with the status
        return leadsRepository.findByStatus(status, pageable).map(leadsMapper::toResponse);
    }

    @Transactional
    public void deleteLead(Long id) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        leadsRepository.delete(leads);
    }

    @Transactional
    public LeadsResponseDTO validateLead(Long id, LeadValidationRequest request) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        User currentUser = authorizationService.getCurrentUser();
        leads.setValidatedBy(currentUser);
        leads.setValidatedAt(java.time.LocalDateTime.now());
        leads.setStatus(request.getStatus());
        
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO initiateContact(Long id, InitiateContactRequestDTO request) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        leadsMapper.updateContactDetails(leads, request);
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO assignLeadToAgent(Long leadId, Long agentId) {
        Leads leads = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
        
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));
        
        // Only managers can assign leads
        if (!authorizationService.hasRole("SALES_MANAGER") && !authorizationService.hasRole("PLATFORM_ADMIN")) {
            throw new AccessDeniedException("Only managers can assign leads to agents");
        }
        
        // Verify the user is actually an agent
        if (!agent.getRole().getName().equals("SALES_AGENT")) {
            throw new IllegalArgumentException("User is not a sales agent");
        }
        
        leads.setAssignedTo(agent);
        leads.setAssignedAt(java.time.LocalDateTime.now());
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    @Transactional
    public LeadsResponseDTO takeLead(Long leadId) {
        User currentUser = authorizationService.getCurrentUser();
        
        // Only agents can take leads
        if (!authorizationService.hasRole("SALES_AGENT")) {
            throw new AccessDeniedException("Only agents can take leads");
        }
        
        Leads leads = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
        
        // Can only take unassigned manager leads
        if (!leads.getAddedByManager() || leads.getAssignedTo() != null) {
            throw new AccessDeniedException("This lead cannot be taken");
        }
        
        leads.setAssignedTo(currentUser);
        leads.setAssignedAt(java.time.LocalDateTime.now());
        leads = leadsRepository.save(leads);
        return leadsMapper.toResponse(leads);
    }

    private void validateStatusTransition(LeadStatus currentStatus, LeadStatus newStatus) {
        // Add your status transition validation logic here
        // For now, we'll allow all transitions
    }

//    public List<LeadsResponseDTO> getMyLeads(Long userId) {
//        User currentUser = authorizationService.getCurrentUser();
//
//        // Agents can only see their own leads and unassigned manager leads
//        if (authorizationService.hasRole("SALES_AGENT")) {
//            List<Leads> myLeads = leadsRepository.findMyLeads(currentUser.getId());
//            List<Leads> unassignedManagerLeads = leadsRepository.findUnassignedManagerLeads();
//
//            List<Leads> allLeads = new java.util.ArrayList<>();
//            allLeads.addAll(myLeads);
//            allLeads.addAll(unassignedManagerLeads);
//
//            return allLeads.stream().map(leadsMapper::toResponse).collect(Collectors.toList());
////        }
//
//        // Managers can see all leads
//        return leadsRepository.findAll().stream().map(leadsMapper::toResponse).collect(Collectors.toList());
//    }

    public Page<LeadsResponseDTO> getMyLeads(Long userId, Pageable pageable) {
        User currentUser = authorizationService.getCurrentUser();
        
        // Agents can see their own leads, unassigned manager leads, and leads assigned to them
        if (authorizationService.hasRole("SALES_AGENT")) {
            List<Leads> myLeads = leadsRepository.findMyLeads(currentUser.getId());
            List<Leads> unassignedManagerLeads = leadsRepository.findUnassignedManagerLeads();
            List<Leads> assignedToMeLeads = leadsRepository.findByAssignedToId(currentUser.getId());
            
            List<Leads> allLeads = new java.util.ArrayList<>();
            allLeads.addAll(myLeads);
            allLeads.addAll(unassignedManagerLeads);
            allLeads.addAll(assignedToMeLeads);
            
            // Apply pagination manually since we're combining two different queries
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allLeads.size());
            
            if (start > allLeads.size()) {
                return new org.springframework.data.domain.PageImpl<>(
                    new java.util.ArrayList<>(),
                    pageable,
                    0
                );
            }
            
            List<Leads> pageContent = allLeads.subList(start, end);
            List<LeadsResponseDTO> responseContent = pageContent.stream()
                .map(leadsMapper::toResponse)
                .collect(Collectors.toList());
            
            return new org.springframework.data.domain.PageImpl<>(
                responseContent,
                pageable,
                allLeads.size()
            );
        }
        
        // Managers can see all leads with pagination
        return leadsRepository.findAll(pageable).map(leadsMapper::toResponse);
    }

//    public List<LeadsResponseDTO> getLeadsByTerritory(Long territoryId) {
//        return leadsRepository.findByTerritoryId(territoryId).stream()
//                .filter(leads -> canAccessLead(leads))
//                .map(leadsMapper::toResponse)
//                .collect(Collectors.toList());
//    }

//    public Page<LeadsResponseDTO> getLeadsByTerritory(Long territoryId, Pageable pageable) {
//        return leadsRepository.findByTerritoryId(territoryId, pageable)
//                .map(leads -> {
//                    if (canAccessLead(leads)) {
//                        return leadsMapper.toResponse(leads);
//                    }
//                    return null;
//                })
//                .filter(response -> response != null);
//    }

//    public List<LeadsResponseDTO> getMyLeadsByStatus(Long userId, LeadStatus status) {
//        User currentUser = authorizationService.getCurrentUser();
//
//        // Agents can only see their own leads and unassigned manager leads
//        if (authorizationService.getCurrentUser().getRole().getName().equals("SALES_AGENT")) {
//            List<Leads> myLeads = leadsRepository.findMyLeadsByStatus(currentUser.getId(), status);
//            List<Leads> unassignedManagerLeads = leadsRepository.findUnassignedManagerLeads().stream()
//                    .filter(lead -> lead.getStatus().equals(status))
//                    .collect(Collectors.toList());
//
//            List<Leads> allLeads = new java.util.ArrayList<>();
//            allLeads.addAll(myLeads);
//            allLeads.addAll(unassignedManagerLeads);
//
//            return allLeads.stream().map(leadsMapper::toResponse).collect(Collectors.toList());
//        }
//
//        // Managers can see all leads with the status
//        return leadsRepository.findByStatus(status, new org.springframework.data.domain.PageRequest(0, Integer.MAX_VALUE))
//                .getContent().stream().map(leadsMapper::toResponse).collect(Collectors.toList());
//    }

//    public List<LeadsResponseDTO> getLeadsByTerritoryAndStatus(Long territoryId, LeadStatus status) {
//        return leadsRepository.findByTerritoryIdAndStatus(territoryId, status).stream()
//                .filter(leads -> canAccessLead(leads))
//                .map(leadsMapper::toResponse)
//                .collect(Collectors.toList());
//    }

    public LeadsResponseDTO getMyLeadById(Long id) {
        Leads leads = leadsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        return leadsMapper.toResponse(leads);
    }

//    public LeadsResponseDTO getLeadByTerritoryAndId(Long territoryId, Long id) {
//        Leads leads = leadsRepository.findByTerritoryIdAndId(territoryId, id)
//                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id + " in territory: " + territoryId));
//
//        // Check access permissions
//        if (!canAccessLead(leads)) {
//            throw new AccessDeniedException("You don't have permission to view this lead");
//        }
//
//        return leadsMapper.toResponse(leads);
//    }



    /**
     * Check for duplicates in business name, phone number, or address
     */
    private boolean checkForDuplicates(String businessName, String phoneNumber, String streetNumber, String streetName, String postalCode) {
        boolean hasDuplicateName = leadsRepository.existsByBusinessName(businessName);
        boolean hasDuplicatePhone = leadsRepository.existsByPhoneNumber(phoneNumber);
        boolean hasDuplicateAddress = leadsRepository.existsByAddress(streetNumber, streetName, postalCode);
        
        return hasDuplicateName || hasDuplicatePhone || hasDuplicateAddress;
    }
} 