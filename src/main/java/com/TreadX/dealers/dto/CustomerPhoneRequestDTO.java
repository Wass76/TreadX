package com.TreadX.dealers.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerPhoneRequestDTO {
    private Long customerId;
    private String phoneNumber;
    private String phoneType;
    private String phoneStatus;
} 