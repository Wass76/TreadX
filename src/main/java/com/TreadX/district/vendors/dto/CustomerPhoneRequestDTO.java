package com.TreadX.district.vendors.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPhoneRequestDTO {
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @NotNull(message = "Phone type is required")
    private com.TreadX.district.vendors.entity.CustomerPhone.PhoneType phoneType;
    
    private String extension;
    
    private String notes;
    
    @Builder.Default
    private Boolean isPrimary = false;
    
    @Builder.Default
    private com.TreadX.district.vendors.entity.CustomerPhone.PhoneStatus phoneStatus = 
        com.TreadX.district.vendors.entity.CustomerPhone.PhoneStatus.ACTIVE;
} 