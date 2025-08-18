package com.TreadX.district.vendors.service;

import com.TreadX.district.vendors.dto.VendorRequestDTO;
import com.TreadX.district.vendors.dto.VendorResponseDTO;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.district.vendors.mapper.VendorMapper;
import com.TreadX.district.vendors.enums.LeadStatus;
import com.TreadX.district.vendors.enums.VendorStatus;
import com.TreadX.user.config.RoleConstants;
import com.TreadX.utils.VendorIdGenerator;
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
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    private final LeadsRepository leadsRepository;

    public Page<VendorResponseDTO> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable)
                .map(vendorMapper::toResponse);
    }

    public VendorResponseDTO getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        return vendorMapper.toResponse(vendor);
    }

    @Transactional
    public VendorResponseDTO createVendor(VendorRequestDTO request) {
        // 1. Validate lead is CONTACTED
        Leads lead = leadsRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + request.getLeadId()));
        if (lead.getStatus() != LeadStatus.CONTACTED) {
            throw new ConflictException("Lead must be in CONTACTED status to create a vendor.");
        }
        
        // 2. Validate user access management data
        validateUserAccessManagement(request);
        
        // 3. Map info from lead and request to vendor
        Vendor vendor = vendorMapper.toEntity(request);
        
        // 4. Save vendor first to get the ID
        vendor = vendorRepository.save(vendor);
        
        // 5. Generate vendorUniqueId using the new format: 001010001 + vendorId
        vendor.setVendorUniqueId(VendorIdGenerator.generateVendorUniqueId(vendor.getId()));
        vendor = vendorRepository.save(vendor);
        
        // 6. Log user access management information
        logUserAccessManagement(vendor, request);
        
        // 7. Update lead status to ONBOARDED
        lead.setStatus(LeadStatus.ONBOARDED);
        leadsRepository.save(lead);
        
        // 8. Return VendorResponseDTO
        return vendorMapper.toResponse(vendor);
    }

    @Transactional
    public VendorResponseDTO updateVendor(Long id, VendorRequestDTO request) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        vendorMapper.updateEntityFromRequest(vendor, request);
        vendor = vendorRepository.save(vendor);
        return vendorMapper.toResponse(vendor);
    }

    /**
     * Partially updates a vendor with only the fields that are present in the request.
     * Fields that are null in the request will not be updated.
     */
    @Transactional
    public VendorResponseDTO updateVendorPartial(Long id, VendorRequestDTO request) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        vendorMapper.updateEntityFromRequestPartial(vendor, request);
        vendor = vendorRepository.save(vendor);
        return vendorMapper.toResponse(vendor);
    }

    @Transactional
    public void deleteVendor(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vendor not found with id: " + id);
        }
        vendorRepository.deleteById(id);
    }

    public Page<VendorResponseDTO> searchVendors(String query, Pageable pageable) {
        return vendorRepository.searchVendors(query, pageable)
                .map(vendorMapper::toResponse);
    }

    public Page<VendorResponseDTO> getVendorsByStatus(VendorStatus status, Pageable pageable) {
        return vendorRepository.findByVendorStatus(status, pageable)
                .map(vendorMapper::toResponse);
    }
    
    /**
     * Validates user access management data
     */
    private void validateUserAccessManagement(VendorRequestDTO request) {
        if (request.getTotalUsers() != null && request.getTotalUsers() <= 0) {
            throw new IllegalArgumentException("Total users must be greater than 0");
        }
        
        if (request.getUserRoles() != null && !request.getUserRoles().isEmpty()) {
            // Validate that user roles are valid vendor roles
            for (Map.Entry<String, Integer> entry : request.getUserRoles().entrySet()) {
                String role = entry.getKey();
                Integer count = entry.getValue();
                
                if (!isValidVendorRole(role)) {
                    throw new IllegalArgumentException("Invalid vendor role: " + role);
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
    private boolean isValidVendorRole(String role) {
        return RoleConstants.VENDOR_ADMIN.equals(role) || 
               RoleConstants.VENDOR_EMPLOYEE.equals(role) || 
               RoleConstants.VENDOR_TECHNICIAN.equals(role);
    }
    
    /**
     * Logs user access management information for later integration
     */
    private void logUserAccessManagement(Vendor vendor, VendorRequestDTO request) {
        log.info("Vendor {} created with user access management:", vendor.getBusinessName());
        log.info("  - Total Users: {}", request.getTotalUsers());
        
        if (request.getUserRoles() != null) {
            log.info("  - User Role Distribution:");
            for (Map.Entry<String, Integer> entry : request.getUserRoles().entrySet()) {
                log.info("    * {}: {} users", entry.getKey(), entry.getValue());
            }
        }
        
        log.info("  - Note: User account creation will be handled by vendor application");
    }
} 