package com.TreadX.address.service;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.entity.Address;
import com.TreadX.address.entity.City;
import com.TreadX.address.entity.Country;
import com.TreadX.address.entity.State;
import com.TreadX.address.entity.SystemCity;
import com.TreadX.address.entity.SystemCountry;
import com.TreadX.address.entity.SystemProvince;
import com.TreadX.address.repository.AddressRepository;
import com.TreadX.address.repository.CityRepository;
import com.TreadX.address.repository.CountryRepository;
import com.TreadX.address.repository.StateRepository;
import com.TreadX.address.repository.SystemCityRepository;
import com.TreadX.address.repository.SystemCountryRepository;
import com.TreadX.address.repository.SystemProvinceRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);
    @Autowired
    private SystemCountryRepository systemCountryRepository;

    @Autowired
    private SystemProvinceRepository systemProvinceRepository;

    @Autowired
    private SystemCityRepository systemCityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(AddressRequestDTO addressRequestDTO) {
        // Find the related entities
        log.info("Creating new address");
        log.info("addressRequestDTO: {}", addressRequestDTO);
        log.info("CountryId: {}", addressRequestDTO.getCountryId());
        Country country = countryRepository.findById(addressRequestDTO.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + addressRequestDTO.getCountryId()));
        log.info("Country: {}", country);
        log.info("StateId: {}", addressRequestDTO.getStateId());
        State state = stateRepository.findById(addressRequestDTO.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + addressRequestDTO.getStateId()));
        log.info("State: {}", state);
        log.info("CityId: {}", addressRequestDTO.getCityId());
        City city = cityRepository.findById(addressRequestDTO.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + addressRequestDTO.getCityId()));
        log.info("City: {}", city);
        // Process system entries
        Map<String,Object> map = processAddressForSystemEntries(country, state, city);

        // Create and save the address
        List<Address> addresses = addressRepository
                .findByStreetNameAndStreetNumberAndCountryAndCityAndProvinceAndUnitNumberAndPostalCode(
                        addressRequestDTO.getStreetName(),
                        addressRequestDTO.getStreetNumber(),
                        (SystemCountry) map.get("systemCountry"),
                        (SystemCity) map.get("systemCity"),
                        (SystemProvince) map.get("systemProvince"),
                        addressRequestDTO.getUnitNumber(),
                        addressRequestDTO.getPostalCode()
                ).orElse(Collections.emptyList());

        if (!addresses.isEmpty()) {
            Address existedAddress = addresses.get(0);
            log.info("Address already exists with id: {}", existedAddress.getId());
            return existedAddress;
        }

        Address address = new Address();
        address.setStreetNumber(addressRequestDTO.getStreetNumber());
        address.setStreetName(addressRequestDTO.getStreetName());
        address.setPostalCode(addressRequestDTO.getPostalCode());
        address.setCity((SystemCity) map.get("systemCity"));
        address.setCountry((SystemCountry) map.get("systemCountry"));
        address.setProvince((SystemProvince) map.get("systemProvince"));

        return addressRepository.save(address);
    }

    public Map<String,Object> processAddressForSystemEntries(Country country, State state, City city) {
        // Process Country
        SystemCountry systemCountry = systemCountryRepository.findByCountryEntity(country)
                .orElseGet(() -> {
                    SystemCountry newSystemCountry = new SystemCountry();
                    newSystemCountry.setCountry(country.getName());
                    Optional<SystemCountry> topCountry = systemCountryRepository.findTopByOrderByCountryUniqueIdDesc();
                    String nextId = generateNextUniqueId(topCountry.map(sc -> String.valueOf(sc.getCountryUniqueId())).orElse(null), 3);
                    newSystemCountry.setCountryUniqueId(Long.parseLong(nextId));
                    newSystemCountry.setCountryEntity(country);
                    return systemCountryRepository.save(newSystemCountry);
                });
        Map<String, Object> map = new HashMap<>();
        map.put("systemCountry", systemCountry);

        // Process Province/State
        SystemProvince systemProvince = systemProvinceRepository.findByProvinceEntity(state)
                .orElseGet(() -> {
                    SystemProvince newSystemProvince = new SystemProvince();
                    newSystemProvince.setProvince(state.getName());
                    Optional<SystemProvince> topProvince = systemProvinceRepository.findTopByOrderByProvinceUniqueIdDesc();
                    String nextId = generateNextUniqueId(topProvince.map(sp -> String.valueOf(sp.getProvinceUniqueId())).orElse(null), 2);
                    newSystemProvince.setProvinceUniqueId(Long.parseLong(nextId));
                    newSystemProvince.setSystemCountry(systemCountry);
                    newSystemProvince.setProvinceEntity(state);
                    return systemProvinceRepository.save(newSystemProvince);
                });
        map.put("systemProvince", systemProvince);

        // Process City
        SystemCity systemCity = systemCityRepository.findByCityEntity(city)
                .orElseGet(() -> {
                    SystemCity newSystemCity = new SystemCity();
                    newSystemCity.setCity(city.getName());
                    newSystemCity.setCityEntity(city);
                    Optional<SystemCity> topCity = systemCityRepository.findTopByOrderByCityUniqueIdDesc();
                    String nextId = generateNextUniqueId(topCity.map(sc -> String.valueOf(sc.getCityUniqueId())).orElse(null), 4);
                    newSystemCity.setCityUniqueId(Long.parseLong(nextId));
                    newSystemCity.setSystemProvince(systemProvince);
                    newSystemCity.setSystemCountry(systemCountry);
                    return systemCityRepository.save(newSystemCity);
                });
        map.put("systemCity", systemCity);
        return map;
    }

    // Helper method to generate the next sequential unique ID with specified number of digits
    private String generateNextUniqueId(String currentMaxId, int numberOfDigits) {
        int numericId = 1; // Default starting ID

        if (currentMaxId != null) {
            try {
                numericId = Integer.parseInt(currentMaxId) + 1;
            } catch (NumberFormatException e) {
                // Handle the case where the existing ID is not in the expected format
                // Log the error or handle it as appropriate
                // Fallback to starting ID
            }
        }

        return String.format("%0" + numberOfDigits + "d", numericId);
    }

    public Optional<SystemCountry> getSystemCountryByEntity(Country country) {
        return systemCountryRepository.findByCountryEntity(country);
    }

    public Optional<SystemProvince> getSystemProvinceByEntity(State state) {
        return systemProvinceRepository.findByProvinceEntity(state);
    }

    public Optional<SystemCity> getSystemCityByEntity(City city) {
        return systemCityRepository.findByCityEntity(city);
    }

    public AddressResponseDTO getAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponseDTO.builder()
                .id(address.getId())
                .streetNumber(address.getStreetNumber())
                .streetName(address.getStreetName())
                .postalCode(address.getPostalCode())
                .city(address.getCity() != null ? address.getCity().getCity() : null)
                .state(address.getProvince() != null ? address.getProvince().getProvince() : null)
                .country(address.getCountry() != null ? address.getCountry().getCountry() : null)
                .build();
    }
}