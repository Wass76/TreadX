package com.TreadX.tire.entity;

import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "package")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Package extends AuditedEntity {
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private String status;
    private Integer maxTires;
    private String features;

    @Override
    protected String getSequenceName() {
        return "package_id_seq";
    }
} 