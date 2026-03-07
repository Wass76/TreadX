package com.TreadX.user.dto;

import lombok.Data;

@Data
public class DealerStaffResponseDTO {
    
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String position;
    private String role;
    private String accessLevel;
    private String status;
    private String notes;
}
