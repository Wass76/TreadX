package com.TreadX.dealers.service;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.user.entity.User;
import org.springframework.stereotype.Service;

/**
 * Service to handle automatic address population from user's base address
 */
@Service
public class AddressAutoPopulationService {

    /**
     * Checks if the user is a sales agent
     */
    public boolean isSalesAgent(User user) {
        return user.getRole() != null && "SALES_AGENT".equals(user.getRole().getName());
    }

    /**
     * Checks if the user has base address information
     */
    public boolean hasBaseAddress(User user) {
        return user.getBaseCountry() != null || user.getBaseState() != null || user.getBaseCity() != null;
    }

    /**
     * Populates missing address fields from user's base address
     * Only populates fields that are not already set in the address request
     */
    public void populateAddressFromUserBaseAddress(AddressRequestDTO addressRequest, User user) {
        // Only populate if the field is not already set
        if (addressRequest.getCountryId() == null && user.getBaseCountry() != null) {
            addressRequest.setCountryId(user.getBaseCountry().getId());
        }
        
        if (addressRequest.getStateId() == null && user.getBaseState() != null) {
            addressRequest.setStateId(user.getBaseState().getId());
        }
        
        if (addressRequest.getCityId() == null && user.getBaseCity() != null) {
            addressRequest.setCityId(user.getBaseCity().getId());
        }
    }

    /**
     * Checks if automatic address population should be applied
     * @param user The current user
     * @return true if the user is a sales agent and has base address information
     */
    public boolean shouldApplyAutoPopulation(User user) {
        return isSalesAgent(user) && hasBaseAddress(user);
    }
} 