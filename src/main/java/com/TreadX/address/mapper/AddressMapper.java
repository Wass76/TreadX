package com.TreadX.address.mapper;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequestDTO request) {
        return Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .province(request.getProvince())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .build();
    }

    public AddressResponseDTO toResponse(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
} 