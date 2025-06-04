package com.TreadX.dealers.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerPhoneRequestDTO {
    private Long customerId;
    private String phoneNumber;
    private String phoneType;
    private LocalDateTime addedDate;
    private LocalDateTime updatedDate;
    private String addedBy;
    private String updatedBy;
    private String phoneStatus;
} 