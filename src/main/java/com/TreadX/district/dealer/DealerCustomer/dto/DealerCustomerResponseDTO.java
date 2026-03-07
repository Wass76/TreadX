package com.TreadX.district.dealer.DealerCustomer.dto;

import com.TreadX.address.dto.AddressResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerCustomerResponseDTO {

    // Basic Information
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    // Address Information
   private AddressResponseDTO address;

    // Phone Numbers
    private String phoneNumber;

    // Vendor Information
    private Long dealerId;          
    private String dealerName;
    private String dealerCustomerUniqueId;

    // Audit Information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
