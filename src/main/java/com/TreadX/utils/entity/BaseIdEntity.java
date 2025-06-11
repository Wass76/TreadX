package com.TreadX.utils.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public abstract class BaseIdEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
        name = "id_sequence",
        sequenceName = "#{T(com.TreadX.utils.entity.BaseIdEntity).getSequenceName()}",
        allocationSize = 1
    )
    private Long id;

    /**
     * This method must be implemented by each entity to provide its own sequence name
     * @return The sequence name for this entity
     */
    protected abstract String getSequenceName();
} 