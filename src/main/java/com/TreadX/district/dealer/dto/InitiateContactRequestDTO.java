package com.TreadX.district.dealer.dto;

import com.TreadX.district.dealer.enums.ContactMethod;

import lombok.Data;

@Data
public class InitiateContactRequestDTO {
    private ContactMethod contactMethod;
    private String contactMethodDetails;
    private String extensionNumber;
    private String contactName;
    private String position;
}