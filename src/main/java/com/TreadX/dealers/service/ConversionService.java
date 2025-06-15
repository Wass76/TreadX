package com.TreadX.dealers.service;

import com.TreadX.address.entity.Address;
import com.TreadX.address.mapper.AddressMapper;
import com.TreadX.dealers.dto.DealerContactRequestDTO;
import com.TreadX.dealers.dto.DealerContactResponseDTO;
import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.entity.DealerContact;
import com.TreadX.dealers.entity.Leads;
import com.TreadX.dealers.mapper.DealerContactMapper;
import com.TreadX.dealers.mapper.DealerMapper;
import com.TreadX.dealers.repository.DealerContactRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.LeadsRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConversionService {
    private static final Logger log = LoggerFactory.getLogger(ConversionService.class);

    @Autowired
    private DealerContactRepository dealerContactRepository;
    
    @Autowired
    private LeadsRepository leadsRepository;
    
    @Autowired
    private DealerRepository dealerRepository;
    
    @Autowired
    private DealerContactMapper dealerContactMapper;
    
    @Autowired
    private DealerMapper dealerMapper;

    @Autowired
    private DealerContactService dealerContactService;

    @Autowired
    private DealerService dealerService;
    @Autowired
    private AddressMapper addressMapper;

    /**
     * Converts a lead to a contact.
     * This method prepares the data from lead and request, then delegates to service layer.
     */
    @Transactional
    public DealerContactResponseDTO convertLeadToContact(Long leadId, DealerContactRequestDTO request) {
        log.info("Converting lead with ID: {} to contact", leadId);
        
        Leads lead = leadsRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));

        // Prepare request with data from lead and request body

        // Set data from request if provided, otherwise use lead data
        request.setBusinessName(request.getBusinessName() != null ? request.getBusinessName() : lead.getBusinessName());
        request.setBusinessEmail(request.getBusinessEmail() != null ? request.getBusinessEmail() : lead.getBusinessEmail());
        request.setBusinessPhone(request.getBusinessPhone() != null ? request.getBusinessPhone() : lead.getPhoneNumber());
        request.setSource(request.getSource() != null ? request.getSource() : lead.getSource());
        request.setNotes(request.getNotes() != null ? request.getNotes() : lead.getNotes());
        request.setOwner(request.getOwner() != null ? request.getOwner() : lead.getCreatedBy());
        request.setConvertedFromLeadId(leadId);

        return dealerContactService.createDealerContact(request);
    }

    /**
     * Converts a contact to a dealer.
     * This method prepares the data from contact and request, then delegates to service layer.
     */
    @Transactional
    public DealerResponseDTO convertContactToDealer(Long contactId, DealerRequestDTO request) {
        log.info("Converting contact with ID: {} to dealer", contactId);
        
        DealerContact contact = dealerContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));

        // Prepare request with data from contact and request body

        // Set data from request if provided, otherwise use contact data
        request.setName(request.getName() != null ? request.getName() : contact.getBusinessName());
        request.setEmail(request.getEmail() != null ? request.getEmail() : contact.getBusinessEmail());
        request.setPhone(request.getPhone() != null ? request.getPhone() : contact.getBusinessPhone());

        return dealerService.createDealer(request , contact.getAddress());
    }

    /**
     * Helper method to check if an address is already associated with a lead or contact
     */
    private boolean isAddressInUse(Address address) {
        return leadsRepository.existsByAddress(address) || 
               dealerContactRepository.existsByAddress(address);
    }
} 