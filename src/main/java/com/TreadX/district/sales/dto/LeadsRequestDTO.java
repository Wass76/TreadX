package com.TreadX.dealers.dto;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.dealers.enums.LeadSource;
import com.TreadX.dealers.enums.LeadStatus;
import com.TreadX.utils.annotation.ValidEnum;
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
public class LeadsRequestDTO {
    @NotBlank(message = "business name is required")
    private String businessName;

    @NotBlank(message = "Business email is required")
    @Email(message = "Invalid email format")
    private String businessEmail;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @ValidEnum(enumClass = LeadStatus.class)
    private LeadSource source;
    @ValidEnum(enumClass = LeadStatus.class)
    private LeadStatus status;
    private String notes;

//    @NotBlank(message = "Dealer ID is required")
    private Long dealerId;

    // Address fields
    private AddressRequestDTO address;
} 