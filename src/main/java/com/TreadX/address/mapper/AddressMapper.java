package com.TreadX.address.mapper;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressMapper {

    public AddressRequestDTO toRequestDTO(Address address) {
        if (address == null) return null;
        
        return AddressRequestDTO.builder()
                .streetName(address.getStreetName())
                .streetNumber(address.getStreetNumber())
                .cityId(address.getCity().getId())
                .countryId(address.getCountry().getId())
                .unitNumber(address.getUnitNumber())
                .stateId(address.getProvince().getId())
                .postalCode(address.getPostalCode())
                .specialInstructions(address.getSpecialInstructions())
                .build();
    }

    public AddressResponseDTO toResponseDTO(Address address) {
        if (address == null) return null;

        return AddressResponseDTO.builder()
                .id(address.getId())
                .streetNumber(address.getStreetNumber())
                .streetName(address.getStreetName())
                .postalCode(address.getPostalCode())
                .city(address.getCity() != null ? address.getCity().getCity() : null)
                .province(address.getProvince() != null ? address.getProvince().getProvince() : null)
                .country(address.getCountry() != null ? address.getCountry().getCountry() : null)
                .specialInstructions(address.getSpecialInstructions())
                .build();
    }
}
