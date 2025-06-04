package com.TreadX.user.response;

import com.TreadX.user.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationResponse {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
//    private String username;
    private Role role;
} 