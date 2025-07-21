package com.TreadX.user.dto;

import com.TreadX.user.Enum.TerritoryLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerritoryRequestDTO {
    
    @NotBlank(message = "Territory code is required")
    @Size(min = 2, max = 10, message = "Territory code must be between 2 and 10 characters")
    private String code;
    
    @NotBlank(message = "Territory name is required")
    @Size(max = 100, message = "Territory name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Territory level is required")
    private TerritoryLevel level;
    
    @Size(max = 10, message = "Parent territory code must not exceed 10 characters")
    private String parentTerritoryCode;
    
    @NotBlank(message = "Database URL is required")
    private String databaseUrl;
    
    @NotBlank(message = "Database name is required")
    @Size(max = 50, message = "Database name must not exceed 50 characters")
    private String databaseName;
    
    @NotBlank(message = "Database username is required")
    @Size(max = 50, message = "Database username must not exceed 50 characters")
    private String databaseUsername;
    
    @NotBlank(message = "Database password is required")
    private String databasePassword;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;
    
    @Size(max = 3, message = "Currency code must not exceed 3 characters")
    private String currency;
} 