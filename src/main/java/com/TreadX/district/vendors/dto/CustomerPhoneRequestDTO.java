package com.TreadX.district.vendors.dto;

import lombok.Data;

@Data
public class CustomerPhoneRequestDTO {
    private Long customerId;
    private String phoneNumber;
    private String phoneType;
    private String phoneStatus;
} 