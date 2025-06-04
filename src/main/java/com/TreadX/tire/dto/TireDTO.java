package com.TreadX.tire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TireDTO {
    private Long id;
    private String tireType;
    private Double treadWidth;
    private Double aspectRatio;
    private String construction;
    private Double diameter;
} 