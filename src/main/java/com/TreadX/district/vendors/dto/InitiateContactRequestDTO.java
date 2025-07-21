package com.TreadX.district.vendors.dto;

import com.TreadX.district.vendors.enums.ContactMethod;
import lombok.Data;

@Data
public class InitiateContactRequestDTO {
    private ContactMethod contactMethod;
    private String contactMethodDetails;
    private String extensionNumber;
    private String contactName;
    private String position;
}