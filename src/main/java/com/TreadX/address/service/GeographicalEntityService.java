package com.TreadX.address.service;

import com.TreadX.address.entity.*;
import com.TreadX.address.repository.*;
import com.TreadX.address.service.UniqueIdUtils;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Unified service for handling geographical entity operations.
 * This service provides a centralized way to get or create system entities
 * from base entity IDs, following the same pattern as AddressService.
 * 
 * This service is designed to be used by both AddressService and UserTerritoryService
 * to eliminate code duplication and ensure consistency.
 * 
 * NOTE: This is a simplified version that provides the foundation for
 * system entity creation while avoiding compilation issues.
 */
@Service
@RequiredArgsConstructor
public class GeographicalEntityService {
    
    private static final Logger log = LoggerFactory.getLogger(GeographicalEntityService.class);
    
    // Repositories for direct access to entities
    private final SystemCountryRepository systemCountryRepository;
    private final SystemProvinceRepository systemProvinceRepository;
    private final SystemCityRepository systemCityRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    
    // Constants for ID generation
    private static final int COUNTRY_ID_LENGTH = 3;
    private static final int PROVINCE_ID_LENGTH = 2;
    private static final int CITY_ID_LENGTH = 4;
    
    /**
     * Get or create system entities from base entity IDs.
     * This method provides the foundation for system entity creation
     * while ensuring production safety.
     * 
     * @param baseCountryId Base country ID (can be null)
     * @param baseProvinceId Base province/state ID (can be null)
     * @param baseCityId Base city ID (can be null)
     * @return SystemEntitiesResult containing the system entities
     * @throws ResourceNotFoundException if any base entity is not found
     * @throws IllegalArgumentException if required dependencies are missing
     */
    @Transactional
    public SystemEntitiesResult getOrCreateSystemEntities(
            Long baseCountryId, Long baseProvinceId, Long baseCityId) {
        
        log.debug("Processing system entities: country={}, province={}, city={}", 
                 baseCountryId, baseProvinceId, baseCityId);
        
        SystemCountry systemCountry = null;
        SystemProvince systemProvince = null;
        SystemCity systemCity = null;
        
        // Process country first (if provided)
        if (baseCountryId != null) {
            Country country = findAndValidateCountry(baseCountryId);
            systemCountry = findOrCreateSystemCountry(country);
        }
        
        // Process province (if provided)
        if (baseProvinceId != null) {
            State state = findAndValidateState(baseProvinceId);
            if (systemCountry == null) {
                throw new IllegalArgumentException("Country must be provided when using province");
            }
            systemProvince = findOrCreateSystemProvince(state, systemCountry);
        }
        
        // Process city (if provided)
        if (baseCityId != null) {
            City city = findAndValidateCity(baseCityId);
            if (systemProvince == null) {
                throw new IllegalArgumentException("Province must be provided when using city");
            }
            systemCity = findOrCreateSystemCity(city, systemProvince, systemCountry);
        }
        
        SystemEntitiesResult result = new SystemEntitiesResult(systemCountry, systemProvince, systemCity);
        
        log.debug("System entities result: country={}, province={}, city={}", 
                 systemCountry != null ? systemCountry.getId() : null,
                 systemProvince != null ? systemProvince.getId() : null,
                 systemCity != null ? systemCity.getId() : null);
        
        return result;
    }
    
    /**
     * Find or create system country
     */
    private SystemCountry findOrCreateSystemCountry(Country country) {
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
    
    /**
     * Find or create system province
     */
    private SystemProvince findOrCreateSystemProvince(State state, SystemCountry systemCountry) {
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
    
    /**
     * Find or create system city
     */
    private SystemCity findOrCreateSystemCity(City city, SystemProvince systemProvince, SystemCountry systemCountry) {
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
    
    /**
     * Find and validate base country
     */
    private Country findAndValidateCountry(Long countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + countryId));
    }
    
    /**
     * Find and validate base state/province
     */
    private State findAndValidateState(Long stateId) {
        return stateRepository.findById(stateId)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + stateId));
    }
    
    /**
     * Find and validate base city
     */
    private City findAndValidateCity(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityId));
    }
    
    /**
     * Find system entities from base entity IDs (does not create).
     * Returns null for any system entity not found.
     * Throws if required dependencies are missing.
     */
    @Transactional(readOnly = true)
    public SystemEntitiesResult findSystemEntities(Long baseCountryId, Long baseProvinceId, Long baseCityId) {
        SystemCountry systemCountry = null;
        SystemProvince systemProvince = null;
        SystemCity systemCity = null;

        // Process country first (if provided)
        if (baseCountryId != null) {
            Country country = findAndValidateCountry(baseCountryId);
            systemCountry = systemCountryRepository.findByCountryEntity(country).orElse(null);
        }

        // Process province (if provided)
        if (baseProvinceId != null) {
            State state = findAndValidateState(baseProvinceId);
            if (systemCountry == null) {
                throw new IllegalArgumentException("Country must be provided when using province");
            }
            systemProvince = systemProvinceRepository.findByProvinceEntity(state).orElse(null);
        }

        // Process city (if provided)
        if (baseCityId != null) {
            City city = findAndValidateCity(baseCityId);
            if (systemProvince == null) {
                throw new IllegalArgumentException("Province must be provided when using city");
            }
            systemCity = systemCityRepository.findByCityEntity(city).orElse(null);
        }

        return new SystemEntitiesResult(systemCountry, systemProvince, systemCity);
    }
    
    /**
     * Result DTO for system entities
     */
    public static class SystemEntitiesResult {
        private final SystemCountry systemCountry;
        private final SystemProvince systemProvince;
        private final SystemCity systemCity;
        
        public SystemEntitiesResult(SystemCountry systemCountry, SystemProvince systemProvince, SystemCity systemCity) {
            this.systemCountry = systemCountry;
            this.systemProvince = systemProvince;
            this.systemCity = systemCity;
        }
        
        public SystemCountry getSystemCountry() {
            return systemCountry;
        }
        
        public SystemProvince getSystemProvince() {
            return systemProvince;
        }
        
        public SystemCity getSystemCity() {
            return systemCity;
        }
    }
} 