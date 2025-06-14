package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.dealers.enums.DealerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private DealerStatus status;
    private Integer accessCount;
    private String dealerUniqueId;
    private AddressResponseDTO address;
} 