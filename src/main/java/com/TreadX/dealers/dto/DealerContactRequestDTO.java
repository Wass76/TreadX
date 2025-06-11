package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.dealers.enums.Channel;
import com.TreadX.dealers.enums.ContactStatus;
import com.TreadX.dealers.enums.LeadSource;
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
    private LeadSource source;
    private ContactStatus status;
    private Long owner;
    private Channel channel;
    private String ex;
    private String position;
    private Long business;
    private String notes;

    // Address information
    private AddressRequestDTO address;
} 