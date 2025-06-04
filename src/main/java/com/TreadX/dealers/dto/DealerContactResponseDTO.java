package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.entity.Address;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.enums.Channel;
import com.TreadX.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

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
    private String source;
    private User owner;
    private Channel channel;
    private String ex;
    private AddressResponseDTO address;
    private String position;
    private Dealer Business;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long lastModifiedBy;
} 