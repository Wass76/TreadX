package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerContactRequestDTO {
    private String firstName;
    private String lastName;
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private Long convertedFromLeadId;  // ID of the lead this contact was converted from
    private String source;
    private Long owner;
    private String channel;
    private String ex;
    private String position;
    private Long business;
    private String notes;

    // Address information
    private AddressRequestDTO address;
} 