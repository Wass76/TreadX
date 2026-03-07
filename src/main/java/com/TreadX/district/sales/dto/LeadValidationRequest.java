package com.TreadX.district.sales.dto;

import com.TreadX.district.dealer.enums.LeadStatus;

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