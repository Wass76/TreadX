package com.TreadX.utils.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

//@Entity
@Data
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "id")
    @SequenceGenerator(name = "id", allocationSize = 1 , sequenceName = "id")
    private Long id;
}
