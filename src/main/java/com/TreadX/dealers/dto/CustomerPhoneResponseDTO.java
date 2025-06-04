package com.TreadX.dealers.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CustomerPhoneResponseDTO {
    private Long phoneId;
    private Long customerId;
    private String phoneNumber;
    private String phoneType;
    private LocalDateTime addedDate;
    private LocalDateTime updatedDate;
    private String addedBy;
    private String updatedBy;
    private String phoneStatus;
} 