package com.TreadX.user.dto;

import com.TreadX.user.Enum.TerritoryLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateWithTerritoryRequestDTO {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<String> roleNames;
    
    // Territory assignments using BASE entity IDs (not system entity IDs)
    private List<TerritoryAssignmentDTO> territories;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TerritoryAssignmentDTO {
        private TerritoryLevel level;
        private Long countryId;    // Base Country ID
        private Long provinceId;   // Base State/Province ID  
        private Long cityId;       // Base City ID
    }
} 