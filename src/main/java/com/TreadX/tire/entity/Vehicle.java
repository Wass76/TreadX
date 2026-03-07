package com.TreadX.tire.entity;

import com.TreadX.district.dealer.DealerCustomer.entity.DealerCustomer;
import com.TreadX.utils.entity.AuditedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "vehicle")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends AuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_customer_id", nullable = false)
    private DealerCustomer dealerCustomer;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "plate_number")
    private String plateNumber;

    @Column(name = "vin", unique = true)
    private String vin;

    @Column(name = "color")
    private String color;

    @Column(name = "odometer_km")
    private Long odometerKm;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tire> tires;

    @Override
    protected String getSequenceName() {
        return "vehicle_id_seq";
    }
}
