package com.TreadX.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateResponseDTO {
    private Long id;
    private String name;
    private String type;
    
    // Include only the ID and name of the country to avoid circular references
    private Long countryId;
    private String countryName;
}
