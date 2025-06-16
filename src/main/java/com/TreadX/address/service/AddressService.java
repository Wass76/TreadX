package com.TreadX.address.service;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.dto.AddressResponseDTO;
import com.TreadX.address.dto.CountryResponseDTO;
import com.TreadX.address.dto.StateResponseDTO;
import com.TreadX.address.dto.CityResponseDTO;
import com.TreadX.address.entity.*;
import com.TreadX.address.mapper.AddressMapper;
import com.TreadX.address.repository.*;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);
    
    // Constants for ID generation
    private static final int COUNTRY_ID_LENGTH = 3;
    private static final int PROVINCE_ID_LENGTH = 2;
    private static final int CITY_ID_LENGTH = 4;

    private final SystemCountryRepository systemCountryRepository;
    private final SystemProvinceRepository systemProvinceRepository;
    private final SystemCityRepository systemCityRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    /**
     * Get all addresses in the system
     */
    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(addressMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all countries in the system
     */
    public List<SystemCountry> getAllCountries() {
        return systemCountryRepository.findAll();
    }

    /**
     * Get all base countries
     */
    public List<CountryResponseDTO> getAllBaseCountries() {
        return countryRepository.findAll().stream()
                .map(country -> CountryResponseDTO.builder()
                        .id(country.getId())
                        .name(country.getName())
                        .iso3(country.getIso3())
                        .latitude(country.getLatitude())
                        .longitude(country.getLongitude())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get all base states
     */
    public List<StateResponseDTO> getAllBaseStates() {
        return stateRepository.findAll().stream()
                .map(state -> StateResponseDTO.builder()
                        .id(state.getId())
                        .name(state.getName())
                        .type(state.getType())
                        .countryId(state.getCountry() != null ? state.getCountry().getId() : null)
                        .countryName(state.getCountry() != null ? state.getCountry().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get all base cities
     */
    public List<CityResponseDTO> getAllBaseCities() {
        return cityRepository.findAll().stream()
                .map(city -> CityResponseDTO.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .latitude(city.getLatitude())
                        .longitude(city.getLongitude())
                        .stateId(city.getState() != null ? city.getState().getId() : null)
                        .stateName(city.getState() != null ? city.getState().getName() : null)
                        .countryId(city.getCountry() != null ? city.getCountry().getId() : null)
                        .countryName(city.getCountry() != null ? city.getCountry().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get all base cities for a specific province/state
     */
    public List<CityResponseDTO> getBaseCitiesByProvince(Long provinceId) {
        State state = stateRepository.findById(provinceId)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + provinceId));
        return cityRepository.findByState(state).stream()
                .map(city -> CityResponseDTO.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .latitude(city.getLatitude())
                        .longitude(city.getLongitude())
                        .stateId(city.getState() != null ? city.getState().getId() : null)
                        .stateName(city.getState() != null ? city.getState().getName() : null)
                        .countryId(city.getCountry() != null ? city.getCountry().getId() : null)
                        .countryName(city.getCountry() != null ? city.getCountry().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get all base cities for a specific country
     */
    public List<CityResponseDTO> getBaseCitiesByCountry(Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + countryId));
        return cityRepository.findByCountry(country).stream()
                .map(city -> CityResponseDTO.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .latitude(city.getLatitude())
                        .longitude(city.getLongitude())
                        .stateId(city.getState() != null ? city.getState().getId() : null)
                        .stateName(city.getState() != null ? city.getState().getName() : null)
                        .countryId(city.getCountry() != null ? city.getCountry().getId() : null)
                        .countryName(city.getCountry() != null ? city.getCountry().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get all base provinces/states for a specific country
     */
    public List<StateResponseDTO> getBaseProvincesByCountry(Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + countryId));
        return stateRepository.findByCountry(country).stream()
                .map(state -> StateResponseDTO.builder()
                        .id(state.getId())
                        .name(state.getName())
                        .type(state.getType())
                        .countryId(state.getCountry() != null ? state.getCountry().getId() : null)
                        .countryName(state.getCountry() != null ? state.getCountry().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get all provinces/states for a specific country
     */
    public List<SystemProvince> getProvincesByCountry(Long countryId) {
        SystemCountry country = systemCountryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + countryId));
        return systemProvinceRepository.findBySystemCountry(country);
    }

    /**
     * Get all cities for a specific province/state
     */
    public List<SystemCity> getCitiesByProvince(Long provinceId) {
        SystemProvince province = systemProvinceRepository.findById(provinceId)
                .orElseThrow(() -> new ResourceNotFoundException("Province not found with id: " + provinceId));
        return systemCityRepository.findBySystemProvince(province);
    }

    /**
     * Get all cities for a specific country
     */
    public List<SystemCity> getCitiesByCountry(Long countryId) {
        SystemCountry country = systemCountryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + countryId));
        return systemCityRepository.findBySystemCountry(country);
    }

    /**
     * Creates a new address or returns an existing one based on the provided DTO.
     * This method handles the creation of system entries (country, province, city) if they don't exist.
     *
     * @param addressRequestDTO The address data to create
     * @return The created or existing address
     * @throws ResourceNotFoundException if any of the required entities (country, state, city) are not found
     */
    @Transactional
    public Address createOrReturnAddress(AddressRequestDTO addressRequestDTO) {
        log.info("Creating new address for request: {}", addressRequestDTO);
        
        // Find and validate required entities
        Country country = findAndValidateCountry(addressRequestDTO.getCountryId());
        State state = findAndValidateState(addressRequestDTO.getStateId());
        City city = findAndValidateCity(addressRequestDTO.getCityId());

        // Process system entries and create address
        Map<String, Object> systemEntries = processAddressForSystemEntries(country, state, city);
        return getOrCreateAddressBySystemEntities((SystemCountry) systemEntries.get("systemCountry"), (SystemProvince) systemEntries.get("systemProvince"), (SystemCity) systemEntries.get("systemCity"), addressRequestDTO);
    }

    /**
     * Gets an existing address or creates a new one based on system entities.
     * This method is used when we already have the system entities and want to ensure
     * we don't create duplicate addresses.
     */
    @Transactional
    public Address getOrCreateAddressBySystemEntities(
            SystemCountry systemCountry,
            SystemProvince systemProvince,
            SystemCity systemCity,
            AddressRequestDTO addressRequestDTO) {
        
        // Try to find existing address with these system entities
        Optional<List<Address>> existingAddresses = addressRepository.findByCountryAndProvinceAndCityAndPostalCodeAndStreetNameAndStreetNumberAndUnitNumber(
            systemCountry, systemProvince, systemCity, addressRequestDTO.getPostalCode(),
            addressRequestDTO.getStreetName(), addressRequestDTO.getStreetNumber(), addressRequestDTO.getUnitNumber());
            
        if (existingAddresses.isPresent() && !existingAddresses.get().isEmpty()) {
            Address existingAddress = existingAddresses.get().get(0);
            log.info("Found existing address with ID: {}", existingAddress.getId());
            return existingAddress;
        }

        // Create new address if none exists
        log.info("No existing address found, creating new address");
        return createAddress(addressRequestDTO, systemCountry, systemProvince, systemCity);
    }

    /**
     * Processes the system entries (country, province, city) for the given entities.
     * Creates new system entries if they don't exist.
     */
    private Map<String, Object> processAddressForSystemEntries(Country country, State state, City city) {
        Map<String, Object> map = new HashMap<>();
        
        SystemCountry systemCountry = processSystemCountry(country);
        map.put("systemCountry", systemCountry);

        SystemProvince systemProvince = processSystemProvince(state, systemCountry);
        map.put("systemProvince", systemProvince);

        SystemCity systemCity = processSystemCity(city, systemProvince, systemCountry);
        map.put("systemCity", systemCity);

        return map;
    }

    private SystemCountry processSystemCountry(Country country) {
        return systemCountryRepository.findByCountryEntity(country)
                .orElseGet(() -> createNewSystemCountry(country));
    }

    private SystemCountry createNewSystemCountry(Country country) {
        Optional<SystemCountry> topCountry = systemCountryRepository.findTopByOrderByCountryUniqueIdDesc();
        String nextId = UniqueIdUtils.generateNextUniqueId(
            topCountry.map(sc -> sc.getCountryUniqueId()).orElse(null),
            COUNTRY_ID_LENGTH
        );

        log.info("Creating new country with ID: {}", nextId);

        SystemCountry newSystemCountry = new SystemCountry();
        newSystemCountry.setCountry(country.getName());
        newSystemCountry.setCountryUniqueId(nextId);
        newSystemCountry.setCountryEntity(country);
        
        return systemCountryRepository.save(newSystemCountry);
    }

    private SystemProvince processSystemProvince(State state, SystemCountry systemCountry) {
        return systemProvinceRepository.findByProvinceEntity(state)
                .orElseGet(() -> createNewSystemProvince(state, systemCountry));
    }

    private SystemProvince createNewSystemProvince(State state, SystemCountry systemCountry) {
        Optional<SystemProvince> topProvince = systemProvinceRepository.findTopBySystemCountryOrderByProvinceUniqueIdDesc(systemCountry);
        String countryId = systemCountry.getCountryUniqueId();
        String nextProvinceId = UniqueIdUtils.generateNextUniqueId(
            topProvince.map(sp -> sp.getProvinceUniqueId().substring(COUNTRY_ID_LENGTH)).orElse(null), 
            PROVINCE_ID_LENGTH
        );

        String provinceUniqueId = countryId + nextProvinceId;
        log.info("Creating new province with uniqueId: {}", provinceUniqueId);

        SystemProvince newSystemProvince = new SystemProvince();
        newSystemProvince.setProvince(state.getName());
        newSystemProvince.setProvinceUniqueId(provinceUniqueId);
        newSystemProvince.setSystemCountry(systemCountry);
        newSystemProvince.setProvinceEntity(state);
        
        return systemProvinceRepository.save(newSystemProvince);
    }

    private SystemCity processSystemCity(City city, SystemProvince systemProvince, SystemCountry systemCountry) {
        return systemCityRepository.findByCityEntity(city)
                .orElseGet(() -> createNewSystemCity(city, systemProvince, systemCountry));
    }

    private SystemCity createNewSystemCity(City city, SystemProvince systemProvince, SystemCountry systemCountry) {
        Optional<SystemCity> topCity = systemCityRepository.findTopBySystemProvinceOrderByCityUniqueIdDesc(systemProvince);
        String provinceId = systemProvince.getProvinceUniqueId();
        String nextCityId = UniqueIdUtils.generateNextUniqueId(
            topCity.map(sc -> sc.getCityUniqueId().substring(COUNTRY_ID_LENGTH + PROVINCE_ID_LENGTH)).orElse(null), 
            CITY_ID_LENGTH
        );

        String cityUniqueId = provinceId + nextCityId;
        log.info("Creating new city with uniqueId: {}", cityUniqueId);

        SystemCity newSystemCity = new SystemCity();
        newSystemCity.setCity(city.getName());
        newSystemCity.setCityEntity(city);
        newSystemCity.setCityUniqueId(cityUniqueId);
        newSystemCity.setSystemProvince(systemProvince);
        newSystemCity.setSystemCountry(systemCountry);
        
        return systemCityRepository.save(newSystemCity);
    }

    private Address createAddress(AddressRequestDTO request, SystemCountry systemCountry, SystemProvince systemProvince, SystemCity systemCity) {
        Address newAddress = new Address();
        newAddress.setStreetName(request.getStreetName());
        newAddress.setStreetNumber(request.getStreetNumber());
        newAddress.setUnitNumber(request.getUnitNumber());
        newAddress.setCity(systemCity);
        newAddress.setProvince(systemProvince);
        newAddress.setCountry(systemCountry);
        newAddress.setPostalCode(request.getPostalCode());
        newAddress.setSpecialInstructions(request.getSpecialInstructions());
        return addressRepository.save(newAddress);
    }

    private Country findAndValidateCountry(Long countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + countryId));
    }

    private State findAndValidateState(Long stateId) {
        return stateRepository.findById(stateId)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + stateId));
    }

    private City findAndValidateCity(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityId));
    }
}