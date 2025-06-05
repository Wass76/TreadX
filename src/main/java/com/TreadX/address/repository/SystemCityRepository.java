package com.TreadX.address.repository;

import com.TreadX.address.entity.City;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemCityRepository extends JpaRepository<SystemCity, Long> {
    Optional<SystemCity> findTopByOrderByCityUniqueIdDesc();
    Optional<SystemCity> findByCity(String city);
    Optional<SystemCity> findByCityEntity(City city);
    Optional<SystemCity> findTopBySystemProvinceOrderByCityUniqueIdDesc(SystemProvince systemProvince);
} 