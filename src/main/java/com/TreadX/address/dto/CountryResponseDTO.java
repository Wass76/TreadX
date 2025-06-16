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
public class CountryResponseDTO {
    private Long id;
    private String name;
    private String iso3;
    private BigDecimal latitude;
    private BigDecimal longitude;
}