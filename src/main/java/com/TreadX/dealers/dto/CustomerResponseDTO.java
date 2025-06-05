package com.TreadX.dealers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private Long dealerId;
    private String customerPhone;
    private String homePhone;
    private String businessPhone;
    private String customerUniqueId;
    private String dealerCustomerUniqueId;
    
    // Address fields
    private String streetNumber;
    private String streetName;
    private String postalCode;
    private String unitNumber;
    private String specialInstructions;
    private String countryName;
    private String stateName;
    private String cityName;
    private String countryUniqueId;
    private String stateUniqueId;
    private String cityUniqueId;
} 