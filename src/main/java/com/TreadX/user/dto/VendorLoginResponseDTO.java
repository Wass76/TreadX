package com.TreadX.user.dto;

import lombok.Data;

@Data
public class VendorLoginResponseDTO {
    
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String email;
    private String role;
    private String vendorId;
    private String vendorName;
}
