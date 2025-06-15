package com.TreadX.address.repository;

import com.TreadX.address.entity.City;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemCityRepository extends JpaRepository<SystemCity, Long> {
    Optional<SystemCity> findTopByOrderByCityUniqueIdDesc();
    Optional<SystemCity> findByCity(String city);
    Optional<SystemCity> findByCityEntity(City city);
    Optional<SystemCity> findTopBySystemProvinceOrderByCityUniqueIdDesc(SystemProvince systemProvince);
    List<SystemCity> findBySystemProvince(SystemProvince systemProvince);
    List<SystemCity> findBySystemCountry(SystemCountry systemCountry);
} 