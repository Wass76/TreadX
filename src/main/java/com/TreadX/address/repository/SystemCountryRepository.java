package com.TreadX.address.repository;

import com.TreadX.address.entity.Country;
import com.TreadX.address.entity.SystemCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemCountryRepository extends JpaRepository<SystemCountry, Long> {
    Optional<SystemCountry> findTopByOrderByCountryUniqueIdDesc();
    Optional<SystemCountry> findByCountryEntity(Country country);
} 