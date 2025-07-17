package com.TreadX.district.vendors.service;

import com.TreadX.district.vendors.dto.VendorRequestDTO;
import com.TreadX.district.vendors.dto.VendorResponseDTO;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.district.sales.entity.Leads;
import com.TreadX.district.sales.repository.LeadsRepository;
import com.TreadX.district.vendors.mapper.VendorMapper;
import com.TreadX.district.vendors.enums.LeadStatus;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    private final LeadsRepository leadsRepository;

    @Transactional
    public VendorResponseDTO createVendor(VendorRequestDTO request) {
        // 1. Validate lead is CONTACTED
        Leads lead = leadsRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + request.getLeadId()));
        if (lead.getStatus() != LeadStatus.CONTACTED) {
            throw new ConflictException("Lead must be in CONTACTED status to create a vendor.");
        }
        // 2. Map info from lead and request to vendor
        Vendor vendor = new Vendor();
        vendor.setLegalName(request.getLegalName());
        vendor.setBusinessName(request.getBusinessName());
        vendor.setStreetNumber(request.getStreetNumber());
        vendor.setStreetName(request.getStreetName());
        vendor.setAptUnitBldg(request.getAptUnitBldg());
        vendor.setPostalCode(request.getPostalCode());
        vendor.setEmail(request.getEmail());
        vendor.setPhoneNumber(request.getPhoneNumber());
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
} 