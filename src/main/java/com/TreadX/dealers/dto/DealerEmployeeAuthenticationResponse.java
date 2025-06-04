package com.TreadX.dealers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealerEmployeeAuthenticationResponse {
    private String token;
    // Add other employee details you want in the response
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
} 