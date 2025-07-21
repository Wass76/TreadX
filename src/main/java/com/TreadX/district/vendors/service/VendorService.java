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
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
        // 2. Map info from lead and request to vendor
        Vendor vendor = vendorMapper.toEntity(request);
        // Generate vendorUniqueId (simple example, customize as needed)
        vendor.setVendorUniqueId("VND-" + System.currentTimeMillis());
        // 3. Save vendor
        vendor = vendorRepository.save(vendor);
        // 4. Update lead status to ONBOARDED
        lead.setStatus(LeadStatus.ONBOARDED);
        leadsRepository.save(lead);
        // 5. Return VendorResponseDTO
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
} 