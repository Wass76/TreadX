package com.TreadX.dealers.dto;

import lombok.Data;

@Data
public class DealerEmployeeResponseDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private Integer accessLevel;
    private String position;
    private String dealerEmployeeNumber;
    private Long dealerId;
    private String dealerEmployeeUniqueId;
} 