package com.TreadX.address.repository;

import com.TreadX.address.entity.City;
import com.TreadX.address.entity.Country;
import com.TreadX.address.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
    List<City> findByState(State state);
    List<City> findByCountry(Country country);
} 