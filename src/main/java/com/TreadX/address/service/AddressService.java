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
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
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

    public Address createOrReturnAddress(AddressRequestDTO addressRequestDTO) {
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
            throw new ConflictException("Address already exists");
//            Address existedAddress = addresses.get(0);
//            log.info("Address already exists with id: {}", existedAddress.getId());
//            return existedAddress;
        }

        Address address = new Address();
        address.setStreetNumber(addressRequestDTO.getStreetNumber());
        address.setStreetName(addressRequestDTO.getStreetName());
        address.setPostalCode(addressRequestDTO.getPostalCode());
        address.setCity((SystemCity) map.get("systemCity"));
        address.setCountry((SystemCountry) map.get("systemCountry"));
        address.setProvince((SystemProvince) map.get("systemProvince"));
        address.setSpecialInstructions(addressRequestDTO.getSpecialInstructions());

        return addressRepository.save(address);
    }

    public Map<String,Object> processAddressForSystemEntries(Country country, State state, City city) {
        // Process Country (3 digits)
        SystemCountry systemCountry = systemCountryRepository.findByCountryEntity(country)
                .orElseGet(() -> {
                    SystemCountry newSystemCountry = new SystemCountry();
                    newSystemCountry.setCountry(country.getName());
                    Optional<SystemCountry> topCountry = systemCountryRepository.findTopByOrderByCountryUniqueIdDesc();
                    String nextId = generateNextUniqueId(topCountry.map(sc -> sc.getCountryUniqueId()).orElse(null), 3);
                    newSystemCountry.setCountryUniqueId(nextId);
                    newSystemCountry.setCountryEntity(country);
                    return systemCountryRepository.save(newSystemCountry);
                });
        Map<String, Object> map = new HashMap<>();
        map.put("systemCountry", systemCountry);

        // Process Province/State (5 digits: countryId + 2 digits)
        SystemProvince systemProvince = systemProvinceRepository.findByProvinceEntity(state)
                .orElseGet(() -> {
                    SystemProvince newSystemProvince = new SystemProvince();
                    newSystemProvince.setProvince(state.getName());
                    // Find the highest province ID for this country
                    Optional<SystemProvince> topProvince = systemProvinceRepository.findTopBySystemCountryOrderByProvinceUniqueIdDesc(systemCountry);
                    String countryId = systemCountry.getCountryUniqueId();
                    String nextProvinceId = generateNextUniqueId(
                        topProvince.map(sp -> sp.getProvinceUniqueId().substring(3)).orElse(null), 
                        2
                    );
                    newSystemProvince.setProvinceUniqueId(countryId + nextProvinceId);
                    newSystemProvince.setSystemCountry(systemCountry);
                    newSystemProvince.setProvinceEntity(state);
                    return systemProvinceRepository.save(newSystemProvince);
                });
        map.put("systemProvince", systemProvince);

        // Process City (9 digits: provinceId + 4 digits)
        SystemCity systemCity = systemCityRepository.findByCityEntity(city)
                .orElseGet(() -> {
                    SystemCity newSystemCity = new SystemCity();
                    newSystemCity.setCity(city.getName());
                    newSystemCity.setCityEntity(city);
                    // Find the highest city ID for this province
                    Optional<SystemCity> topCity = systemCityRepository.findTopBySystemProvinceOrderByCityUniqueIdDesc(systemProvince);
                    String provinceId = systemProvince.getProvinceUniqueId();
                    String nextCityId = generateNextUniqueId(
                        topCity.map(sc -> sc.getCityUniqueId().substring(5)).orElse(null), 
                        4
                    );
                    newSystemCity.setCityUniqueId(provinceId + nextCityId);
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

    // Helper methods to extract IDs from hierarchical IDs
    private String extractCountryId(String hierarchicalId) {
        if (hierarchicalId == null) return null;
        return hierarchicalId.split("-")[0];
    }

    private String extractProvinceId(String hierarchicalId) {
        if (hierarchicalId == null) return null;
        return hierarchicalId.split("-")[1];
    }

    private String extractCityId(String hierarchicalId) {
        if (hierarchicalId == null) return null;
        return hierarchicalId.split("-")[2];
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
                .province(address.getProvince() != null ? address.getProvince().getProvince() : null)
                .country(address.getCountry() != null ? address.getCountry().getCountry() : null)
                .specialInstructions(address.getSpecialInstructions())
                .build();
    }
}