package com.TreadX.user.service;

import com.TreadX.user.dto.TerritoryRequestDTO;
import com.TreadX.user.dto.TerritoryResponseDTO;
import com.TreadX.user.entity.Territory;
import com.TreadX.user.Enum.TerritoryLevel;
import com.TreadX.user.mapper.TerritoryMapper;
import com.TreadX.user.repository.TerritoryRepository;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.utils.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.TreadX.user.service.AuthorizationService;
import com.TreadX.user.entity.User;
import com.TreadX.user.service.UserTerritoryService;
import com.TreadX.address.service.UniqueIdUtils;
import com.TreadX.user.constants.TerritoryIdConstants;

@Service
@RequiredArgsConstructor
public class TerritoryService {
    
    private static final Logger log = LoggerFactory.getLogger(TerritoryService.class);

    @Autowired
    private TerritoryRepository territoryRepository;
    @Autowired
    private TerritoryMapper territoryMapper;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserTerritoryService userTerritoryService;
    // private final SecurityContextService securityContextService; // Will be used later for authorization
    
    /**
     * Create a new territory
     */
    @Transactional
    public TerritoryResponseDTO createTerritory(TerritoryRequestDTO request) {
        log.info("Creating new territory with code: {}", request.getCode());
        
        User currentUser = authorizationService.getCurrentUser();
        String roleName = currentUser.getRole().getName();
        if ("SALES_MANAGER".equals(roleName)) {
            // Only allow creation if parentTerritoryCode is managed by this user
            if (request.getParentTerritoryCode() == null) {
                throw new UnAuthorizedException("Sales manager can only create child territories, not root territories");
            }
            // Get all accessible territories for this user
            List<Territory> accessible = userTerritoryService.getAllAccessibleTerritories(currentUser.getId());
            boolean canCreate = accessible.stream()
                .anyMatch(t -> t.getCode().equals(request.getParentTerritoryCode()));
            if (!canCreate) {
                throw new UnAuthorizedException("You can only create territories under your managed territories.");
            }
        }
        
        // Check if territory code already exists
        if (territoryRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Territory with code '" + request.getCode() + "' already exists");
        }
        
        // Validate parent territory if provided
        if (request.getParentTerritoryCode() != null) {
            validateParentTerritory(request.getParentTerritoryCode(), request.getLevel());
        }
        
        // Create territory entity
        Territory territory = territoryMapper.toEntity(request);
        // Set uniqueId if not present
        if (territory.getUniqueId() == null || territory.getUniqueId().isEmpty()) {
            String uniqueId = generateTerritoryUniqueId(territory, request);
            territory.setUniqueId(uniqueId);
        }
        // Set parentUniqueId if parent exists
        if (territory.getParentTerritoryCode() != null) {
            Territory parent = territoryRepository.findByCode(territory.getParentTerritoryCode())
                .orElse(null);
            if (parent != null) {
                territory.setParentUniqueId(parent.getUniqueId());
            }
        }
        Territory savedTerritory = territoryRepository.save(territory);
        
        log.info("Successfully created territory: {}", savedTerritory.getCode());
        return territoryMapper.toResponseDTO(savedTerritory);
    }
    
    /**
     * Generate uniqueId for territory based on its level and parent
     */
    private String generateTerritoryUniqueId(Territory territory, TerritoryRequestDTO request) {
        return switch (territory.getLevel()) {
            case COUNTRY -> generateCountryUniqueId();
            case PROVINCE -> generateProvinceUniqueId(request);
            case CITY -> generateCityUniqueId(request);
            case DISTRICT -> generateDistrictUniqueId(request);
            default ->
                    throw new IllegalArgumentException("Unsupported territory level for uniqueId generation: " + territory.getLevel());
        };
    }

    private String generateCountryUniqueId() {
        String maxCountryId = territoryRepository.findByLevelAndIsActiveTrue(TerritoryLevel.COUNTRY).stream()
                .map(Territory::getUniqueId)
                .filter(id -> id != null && id.length() == TerritoryIdConstants.COUNTRY_UNIQUE_ID_LENGTH)
                .max(String::compareTo)
                .orElse(null);
        return UniqueIdUtils.generateNextUniqueId(maxCountryId, TerritoryIdConstants.COUNTRY_UNIQUE_ID_LENGTH);
    }

    private String generateProvinceUniqueId(TerritoryRequestDTO request) {
        Territory parentCountry = territoryRepository.findByCode(request.getParentTerritoryCode())
                .orElseThrow(() -> new ResourceNotFoundException("Parent country not found: " + request.getParentTerritoryCode()));
        String countryId = parentCountry.getUniqueId();
        String maxProvinceId = territoryRepository.findByLevelAndParentTerritoryCodeAndIsActiveTrue(TerritoryLevel.PROVINCE, parentCountry.getCode()).stream()
                .map(t -> t.getUniqueId() != null && t.getUniqueId().length() == TerritoryIdConstants.PROVINCE_UNIQUE_ID_LENGTH ? t.getUniqueId().substring(TerritoryIdConstants.COUNTRY_UNIQUE_ID_LENGTH, TerritoryIdConstants.PROVINCE_UNIQUE_ID_LENGTH) : null)
                .filter(id -> id != null)
                .max(String::compareTo)
                .orElse(null);
        String provinceId = UniqueIdUtils.generateNextUniqueId(maxProvinceId, TerritoryIdConstants.PROVINCE_UNIQUE_ID_LENGTH - TerritoryIdConstants.COUNTRY_UNIQUE_ID_LENGTH);
        return countryId + provinceId;
    }

    private String generateCityUniqueId(TerritoryRequestDTO request) {
        Territory parentProvince = territoryRepository.findByCode(request.getParentTerritoryCode())
                .orElseThrow(() -> new ResourceNotFoundException("Parent province not found: " + request.getParentTerritoryCode()));
        String provinceUniqueId = parentProvince.getUniqueId();
        String maxCityId = territoryRepository.findByLevelAndParentTerritoryCodeAndIsActiveTrue(TerritoryLevel.CITY, parentProvince.getCode()).stream()
                .map(t -> t.getUniqueId() != null && t.getUniqueId().length() == TerritoryIdConstants.CITY_UNIQUE_ID_LENGTH ? t.getUniqueId().substring(TerritoryIdConstants.PROVINCE_UNIQUE_ID_LENGTH, TerritoryIdConstants.CITY_UNIQUE_ID_LENGTH) : null)
                .filter(id -> id != null)
                .max(String::compareTo)
                .orElse(null);
        String cityId = UniqueIdUtils.generateNextUniqueId(maxCityId, TerritoryIdConstants.CITY_UNIQUE_ID_LENGTH - TerritoryIdConstants.PROVINCE_UNIQUE_ID_LENGTH);
        return provinceUniqueId + cityId;
    }

    private String generateDistrictUniqueId(TerritoryRequestDTO request) {
        Territory parentCity = territoryRepository.findByCode(request.getParentTerritoryCode())
                .orElseThrow(() -> new ResourceNotFoundException("Parent city not found: " + request.getParentTerritoryCode()));
        String cityUniqueId = parentCity.getUniqueId();
        List<String> districtIds = territoryRepository.findByLevelAndParentTerritoryCodeAndIsActiveTrue(TerritoryLevel.DISTRICT, parentCity.getCode()).stream()
                .map(Territory::getUniqueId)
                .filter(id -> id != null && id.startsWith(cityUniqueId))
                .collect(java.util.stream.Collectors.toList());
        int minLength = TerritoryIdConstants.DISTRICT_INITIAL_UNIQUE_ID_LENGTH;
        int maxLength = districtIds.stream().mapToInt(String::length).max().orElse(minLength);
        String maxDistrictId = districtIds.stream()
                .filter(id -> id.length() == maxLength)
                .map(id -> id.substring(cityUniqueId.length()))
                .max(String::compareTo)
                .orElse(null);
        int districtDigits = maxLength - cityUniqueId.length();
        String nextDistrictId = UniqueIdUtils.generateNextUniqueId(maxDistrictId, districtDigits);
        // If all 9s, open a new digit
        if (maxDistrictId != null && maxDistrictId.chars().allMatch(c -> c == '9')) {
            districtDigits += 1;
            nextDistrictId = "1" + "0".repeat(districtDigits - 1);
        }
        return cityUniqueId + nextDistrictId;
    }
    
    /**
     * Get territory by code
     */
    public TerritoryResponseDTO getTerritoryByCode(String code) {
        log.info("Getting territory by code: {}", code);
        
        Territory territory = territoryRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found with code: " + code));
        
        return territoryMapper.toResponseDTO(territory);
    }
    
    /**
     * Get territory by code with hierarchical data
     */
    public TerritoryResponseDTO getTerritoryByCodeWithHierarchy(String code) {
        log.info("Getting territory by code with hierarchy: {}", code);
        
        Territory territory = territoryRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found with code: " + code));
        
        // Get hierarchical data using simplified methods
        List<String> childTerritoryCodes = territoryRepository.findDirectChildTerritoryCodes(code);
        List<String> descendantTerritoryCodes = getDescendantTerritoryCodes(code);
        List<String> ancestorTerritoryCodes = getAncestorTerritoryCodes(code);
        
        return territoryMapper.toResponseDTOWithHierarchy(territory, childTerritoryCodes, 
                descendantTerritoryCodes, ancestorTerritoryCodes);
    }
    
    /**
     * Get all active territories
     */
    public List<TerritoryResponseDTO> getAllActiveTerritories() {
        log.info("Getting all active territories");
        
        List<Territory> territories = territoryRepository.findByIsActiveTrue();
        return territoryMapper.toResponseDTOList(territories);
    }
    
    /**
     * Get territories by level
     */
    public List<TerritoryResponseDTO> getTerritoriesByLevel(TerritoryLevel level) {
        log.info("Getting territories by level: {}", level);
        
        List<Territory> territories = territoryRepository.findByLevelAndIsActiveTrue(level);
        return territoryMapper.toResponseDTOList(territories);
    }
    
    /**
     * Get child territories by parent code
     */
    public List<TerritoryResponseDTO> getChildTerritories(String parentCode) {
        log.info("Getting child territories for parent: {}", parentCode);
        
        List<Territory> territories = territoryRepository.findByParentTerritoryCodeAndIsActiveTrue(parentCode);
        return territoryMapper.toResponseDTOList(territories);
    }
    
    /**
     * Update territory
     */
    @Transactional
    public TerritoryResponseDTO updateTerritory(String code, TerritoryRequestDTO request) {
        log.info("Updating territory with code: {}", code);
        
        Territory territory = territoryRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found with code: " + code));
        
        // Validate parent territory if provided
        if (request.getParentTerritoryCode() != null) {
            validateParentTerritory(request.getParentTerritoryCode(), request.getLevel());
        }
        
        // Update territory
        territoryMapper.updateEntityFromDTO(territory, request);
        Territory updatedTerritory = territoryRepository.save(territory);
        
        log.info("Successfully updated territory: {}", updatedTerritory.getCode());
        return territoryMapper.toResponseDTO(updatedTerritory);
    }
    
    /**
     * Activate territory
     */
    @Transactional
    public TerritoryResponseDTO activateTerritory(String code) {
        log.info("Activating territory with code: {}", code);
        
        Territory territory = territoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found with code: " + code));
        
        territory.setIsActive(true);
        Territory activatedTerritory = territoryRepository.save(territory);
        
        log.info("Successfully activated territory: {}", activatedTerritory.getCode());
        return territoryMapper.toResponseDTO(activatedTerritory);
    }
    
    /**
     * Deactivate territory
     */
    @Transactional
    public TerritoryResponseDTO deactivateTerritory(String code) {
        log.info("Deactivating territory with code: {}", code);
        
        Territory territory = territoryRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found with code: " + code));
        
        // Check if territory has active children
        List<Territory> activeChildren = territoryRepository.findByParentTerritoryCodeAndIsActiveTrue(code);
        if (!activeChildren.isEmpty()) {
            throw new ConflictException("Cannot deactivate territory with active child territories: " + 
                    activeChildren.stream().map(Territory::getCode).toList());
        }
        
        territory.setIsActive(false);
        Territory deactivatedTerritory = territoryRepository.save(territory);
        
        log.info("Successfully deactivated territory: {}", deactivatedTerritory.getCode());
        return territoryMapper.toResponseDTO(deactivatedTerritory);
    }
    
    /**
     * Get database connection info for territory (for internal use only)
     * Note: This method is deprecated. Use TerritoryDataSourceLookup instead to avoid circular dependency.
     */
    @Deprecated
    public Territory getTerritoryForDatabaseConnection(String code) {
        log.debug("Getting database connection info for territory: {}", code);
        
        return territoryRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found with code: " + code));
    }
    
    /**
     * Get all active territory codes
     */
    public List<String> getAllActiveTerritoryCodes() {
        return territoryRepository.findAllActiveTerritoryCodes();
    }
    
    /**
     * Get territory codes by level
     */
    public List<String> getTerritoryCodesByLevel(TerritoryLevel level) {
        return territoryRepository.findTerritoryCodesByLevel(level);
    }
    
    /**
     * Validate parent territory
     */
    private void validateParentTerritory(String parentCode, TerritoryLevel childLevel) {
        Territory parentTerritory = territoryRepository.findByCodeAndIsActiveTrue(parentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Parent territory not found with code: " + parentCode));
        
        // Validate hierarchy: parent level should be higher than child level
        if (!isValidParentLevel(parentTerritory.getLevel(), childLevel)) {
            throw new UnAuthorizedException("Invalid parent-child relationship: " + 
                    parentTerritory.getLevel() + " cannot be parent of " + childLevel);
        }
    }
    
    /**
     * Check if parent level is valid for child level
     */
    private boolean isValidParentLevel(TerritoryLevel parentLevel, TerritoryLevel childLevel) {
        return switch (childLevel) {
            case DISTRICT -> parentLevel == TerritoryLevel.CITY;
            case CITY -> parentLevel == TerritoryLevel.PROVINCE;
            case PROVINCE -> parentLevel == TerritoryLevel.COUNTRY;
            case COUNTRY -> false; // Country has no parent
        };
    }
    
    /**
     * Get all descendant territory codes (recursive)
     */
    public List<String> getDescendantTerritoryCodes(String rootCode) {
        List<String> descendants = new java.util.ArrayList<>();
        getDescendantTerritoryCodesRecursive(rootCode, descendants);
        return descendants;
    }
    
    /**
     * Recursive helper method for getting descendants
     */
    private void getDescendantTerritoryCodesRecursive(String parentCode, List<String> descendants) {
        List<String> directChildren = territoryRepository.findDirectChildTerritoryCodes(parentCode);
        for (String childCode : directChildren) {
            descendants.add(childCode);
            getDescendantTerritoryCodesRecursive(childCode, descendants);
        }
    }
    
    /**
     * Get all ancestor territory codes (recursive)
     */
    public List<String> getAncestorTerritoryCodes(String childCode) {
        List<String> ancestors = new java.util.ArrayList<>();
        getAncestorTerritoryCodesRecursive(childCode, ancestors);
        return ancestors;
    }
    
    /**
     * Recursive helper method for getting ancestors
     */
    private void getAncestorTerritoryCodesRecursive(String childCode, List<String> ancestors) {
        Territory territory = territoryRepository.findByCodeAndIsActiveTrue(childCode).orElse(null);
        if (territory != null && territory.getParentTerritoryCode() != null) {
            String parentCode = territory.getParentTerritoryCode();
            ancestors.add(parentCode);
            getAncestorTerritoryCodesRecursive(parentCode, ancestors);
        }
    }
} 