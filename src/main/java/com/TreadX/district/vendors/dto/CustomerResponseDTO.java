package com.TreadX.district.vendors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    
    // Basic Information
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    
    // Address Information
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    
    // Phone Numbers
    private List<CustomerPhoneResponseDTO> phoneNumbers;
    
    // Vendor Information
    private Long vendorId;
    private String vendorName;
    private String customerUniqueId;
    
    // Audit Information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
} 