package com.TreadX.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateWithTerritoryResponseDTO {
    
    private UserResponseDTO user;
    private List<UserTerritoryResponseDTO> territories;
    private String message;
} 