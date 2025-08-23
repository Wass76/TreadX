package com.TreadX.district.vendors.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    
    // Basic Information
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    // Address Information (Embedded for vendor portal simplicity)
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    
    // Phone Numbers
    @Valid
    @NotNull(message = "At least one phone number is required")
    private List<CustomerPhoneRequestDTO> phoneNumbers;

} 