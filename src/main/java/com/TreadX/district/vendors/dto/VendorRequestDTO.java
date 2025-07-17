package com.TreadX.district.vendors.dto;

import lombok.Data;

@Data
public class VendorRequestDTO {
    private Long leadId;
    private String legalName;
    private String businessName;
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    private String email;
    private String phoneNumber;
    // Add other fields as needed from your flowchart
} 