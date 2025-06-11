package com.TreadX.tire.entity;

import com.TreadX.dealers.entity.Dealer;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "dealer_packages")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DealerPackages extends AuditedEntity {
    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private Package aPackage;

    private String status;
    private Double price;
    private String description;

    @Override
    protected String getSequenceName() {
        return "dealer_packages_id_seq";
    }
} 