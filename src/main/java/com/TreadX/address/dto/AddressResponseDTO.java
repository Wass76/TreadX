package com.TreadX.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private Long id;
    private String streetName;
    private String streetNumber;
    private String postalCode;
    private String unitNumber;
    private String city;
    private String province;
    private String country;
    private String specialInstructions;
}
 