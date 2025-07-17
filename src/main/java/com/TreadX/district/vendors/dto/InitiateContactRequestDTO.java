package com.TreadX.district.vendors.dto;

import com.TreadX.district.vendors.enums.ContactMethod;
import lombok.Data;

@Data
public class InitiateContactRequestDTO {
    private ContactMethod contactMethod;
    private String otherDetails; // Optional, for 'OTHER'
} 