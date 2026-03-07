package com.TreadX.district.dealer.DealerCustomer.dto;

import com.TreadX.address.dto.AddressRequestDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerCustomerRequestDTO {

    // Basic Information
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Address Information (Embedded for dealer portal simplicity)
    private AddressRequestDTO address;

    @NotNull(message = "Phone number is required")       
    private String phoneNumber;

}
