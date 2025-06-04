package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.entity.Address;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.enums.Channel;
import com.TreadX.user.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerContactRequestDTO {
    private String firstName;
    private String lastName;
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private String source;
    private Long owner;
    private String channel;
    private String ex;
    private String position;
    private Long Business;
    private String notes;

    // Address information
    private AddressRequestDTO address;
} 