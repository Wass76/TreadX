package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.enums.Channel;
import com.TreadX.dealers.enums.ContactStatus;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerContactResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private LeadSource source;
    private ContactStatus status;
    private Long ownerId;
    private String ownerName;
    private Channel channel;
    private String ex;
    private AddressResponseDTO address;
    private String position;
//    private DealerResponseDTO business;
    private Long dealerId;
    private String dealerUniqueId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long lastModifiedBy;
} 