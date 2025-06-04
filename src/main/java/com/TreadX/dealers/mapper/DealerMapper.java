package com.TreadX.dealers.mapper;

import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.mapper.AddressMapper;
import com.TreadX.dealers.dto.DealerRequestDTO;
import com.TreadX.dealers.dto.DealerResponseDTO;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.enums.DealerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealerMapper {

    private final AddressMapper addressMapper;

    public Dealer toEntity(DealerRequestDTO request) {
        return Dealer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .accessCount(request.getAccessCount())
                .status(DealerStatus.valueOf(request.getStatus() != null ? request.getStatus() : "ACTIVE"))
                .build();
    }

    public DealerResponseDTO toResponse(Dealer dealer) {
        DealerResponseDTO response = DealerResponseDTO.builder()
                .id(dealer.getId())
                .name(dealer.getName())
                .email(dealer.getEmail())
                .phone(dealer.getPhone())
                .status(String.valueOf(dealer.getStatus()))
                .accessCount(dealer.getAccessCount())
                .dealerUniqueId(dealer.getDealerUniqueId())
                .build();

        if (dealer.getAddress() != null) {
            response.setAddress(addressMapper.toResponse(dealer.getAddress()));
        }

        return response;
    }

    public void updateEntityFromRequest(Dealer dealer, DealerRequestDTO request) {
        dealer.setName(request.getName());
        dealer.setEmail(request.getEmail());
        dealer.setPhone(request.getPhone());
        dealer.setAccessCount(request.getAccessCount());
        if (request.getStatus() != null) {
            dealer.setStatus(DealerStatus.valueOf(request.getStatus()));
        }
    }
} 