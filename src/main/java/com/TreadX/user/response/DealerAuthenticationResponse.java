package com.TreadX.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealerAuthenticationResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;

}
