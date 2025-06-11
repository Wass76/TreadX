package com.TreadX.tire.entity;

import com.TreadX.dealers.entity.Customer;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "tire")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Tire extends AuditedEntity {
    private String tireType;
    private Double treadWidth;
    private Double aspectRatio;
    private String construction;
    private String composition;
    private Double diameter;
    private Double mileage;
    private String treadCondition;
    private String status;
    private LocalDateTime addedDate;
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "customerUniqueId", referencedColumnName = "customerUniqueId")
    private Customer customerUniqueId;

    @Column(unique = true)
    private String tireUniqueId;

    private String brand;
    private String model;
    private String size;
    private String type;
    private Double price;
    private Integer stock;
    private String description;

    @Override
    protected String getSequenceName() {
        return "tire_id_seq";
    }
}
