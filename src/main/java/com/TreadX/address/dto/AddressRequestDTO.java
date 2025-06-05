package com.TreadX.address.dto;

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
public class AddressRequestDTO {

    @NotBlank(message = "Street name is required")
    private String streetName;

    private String streetNumber;
    private String postalCode;
    private String unitNumber;
    private String specialInstructions;

    @NotNull(message = "City is required")
    private Long cityId;
    @NotNull(message = "State/Province is required")
    private Long stateId;
    @NotNull(message = "Country is required")
    private Long countryId;

}