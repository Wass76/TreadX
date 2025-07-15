package com.TreadX.district.sales.dto;

import com.TreadX.dealers.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadValidationRequest {
    private LeadStatus status;
    private String notes;
} 