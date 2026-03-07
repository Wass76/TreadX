package com.TreadX.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DealerStaffUpdateRequestDTO {
    
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;
    
    private String role;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
