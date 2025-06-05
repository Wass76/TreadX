package com.TreadX.address.repository;

import com.TreadX.address.entity.State;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemProvinceRepository extends JpaRepository<SystemProvince, Long> {
    Optional<SystemProvince> findTopByOrderByProvinceUniqueIdDesc();
    Optional<SystemProvince> findByProvinceEntity(State state);
    Optional<SystemProvince> findTopBySystemCountryOrderByProvinceUniqueIdDesc(SystemCountry systemCountry);
} 