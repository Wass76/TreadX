package com.TreadX.address.repository;

import com.TreadX.address.entity.Country;
import com.TreadX.address.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {
    Optional<State> findByName(String name);
    List<State> findByCountry(Country country);
} 