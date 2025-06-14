package com.TreadX.address.repository;

import com.TreadX.address.entity.Address;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<List<Address>> findByCountryAndProvinceAndCityAndPostalCodeAndStreetNameAndStreetNumberAndUnitNumber(SystemCountry country,SystemProvince province, SystemCity city,  String postalCode ,String streetName, String streetNumber, String unitNumber);
    Optional<Address> findByCityAndProvinceAndCountry(SystemCity city, SystemProvince province, SystemCountry country);
}
