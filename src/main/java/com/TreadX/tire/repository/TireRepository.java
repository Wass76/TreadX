package com.TreadX.tire.repository;

import com.TreadX.tire.entity.Tire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TireRepository extends JpaRepository<Tire, Long> {
} 