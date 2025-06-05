package com.TreadX.address.mapper;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

//    public Address toEntity(AddressRequestDTO request) {
//        return Address.builder()
//                .streetName(request.getStreetName())
//                .streetNumber(request.getStreetNumber())
//                .city(request.getCityId())
//                .province(request.getProvince())
//                .country(request.getCountry())
//                .postalCode(request.getPostalCode())
//                .build();
//    }

    public AddressResponseDTO toResponse(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponseDTO.builder()
                .id(address.getId())
                .streetNumber(address.getStreetNumber())
                .streetName(address.getStreetName())
                .postalCode(address.getPostalCode())
                .city(address.getCity() != null ? address.getCity().getCity() : null)
                .province(address.getProvince() != null ? address.getProvince().getProvince() : null)
                .country(address.getCountry() != null ? address.getCountry().getCountry() : null)
                .build();
    }
    }
