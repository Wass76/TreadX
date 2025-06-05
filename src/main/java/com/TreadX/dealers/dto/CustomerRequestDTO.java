package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Long dealerId;
    private String customerPhone;
    private String homePhone;
    private String businessPhone;
    private String customerUniqueId;
    
    // Address fields
    private AddressRequestDTO address;
} 