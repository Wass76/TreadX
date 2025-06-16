package com.TreadX.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityResponseDTO {
    private Long id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long stateId;
    private String stateName;
    private Long countryId;
    private String countryName;
}
