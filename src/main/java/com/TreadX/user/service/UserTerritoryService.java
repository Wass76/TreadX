package com.TreadX.user.service;

import com.TreadX.address.service.GeographicalEntityService;
import com.TreadX.user.dto.UserTerritoryRequestDTO;
import com.TreadX.user.dto.UserTerritoryResponseDTO;
import com.TreadX.user.entity.TerritoryLevel;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.UserTerritory;
import com.TreadX.user.mapper.UserTerritoryMapper;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.UserTerritoryRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.TreadX.utils.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTerritoryService {
    
    private static final Logger log = LoggerFactory.getLogger(UserTerritoryService.class);
    
    private final UserTerritoryRepository userTerritoryRepository;
    private final UserRepository userRepository;
    private final UserTerritoryMapper userTerritoryMapper;
    private final GeographicalAuthorizationService geographicalAuthService;
    
    // Use the unified GeographicalEntityService
    private final GeographicalEntityService geographicalEntityService;
    
    /**
     * Assign territories to a user using base entity IDs.
     * This method uses the unified GeographicalEntityService to process
     * base entity IDs and create or find corresponding system entities.
     * 
     * @param userId The user ID to assign territories to
     * @param territoryRequests List of territory assignments
     * @return List of created UserTerritory entities
     */
    @Transactional
    public List<UserTerritory> assignTerritoriesToUser(Long userId, List<UserTerritoryRequestDTO> territoryRequests) {
        // Authorization: Only PLATFORM_ADMIN or SALES_MANAGER can assign territories, or user must have access to the territory
        User currentUser = geographicalAuthService.getCurrentUser();
        boolean isAdmin = currentUser.getRole().getName().equals("PLATFORM_ADMIN");
        boolean isSalesManager = currentUser.getRole().getName().equals("SALES_MANAGER");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<UserTerritory> createdTerritories = new ArrayList<>();

        for (UserTerritoryRequestDTO request : territoryRequests) {
            try {
                // Check access to the requested territory if not admin or sales manager
                if (!isAdmin && !isSalesManager) {
                    boolean hasAccess = checkLocationAccess(request.getBaseCityId(), request.getBaseProvinceId(), request.getBaseCountryId());
                    if (!hasAccess) {
                        throw new UnAuthorizedException("You do not have access to the requested territory");
                    }
                }
                // Use the unified GeographicalEntityService to process base entity IDs
                GeographicalEntityService.SystemEntitiesResult systemEntities = 
                    geographicalEntityService.getOrCreateSystemEntities(
                        request.getBaseCountryId(),
                        request.getBaseProvinceId(), 
                        request.getBaseCityId()
                    );
                // Create UserTerritory based on the provided territory level
                UserTerritory userTerritory = createUserTerritoryFromRequest(user, request, systemEntities);
                UserTerritory savedTerritory = userTerritoryRepository.save(userTerritory);
                createdTerritories.add(savedTerritory);
                log.info("Assigned territory to user {}: level={}, country={}, province={}, city={}", 
                         userId, request.getTerritoryLevel(), 
                         systemEntities.getSystemCountry() != null ? systemEntities.getSystemCountry().getId() : null,
                         systemEntities.getSystemProvince() != null ? systemEntities.getSystemProvince().getId() : null,
                         systemEntities.getSystemCity() != null ? systemEntities.getSystemCity().getId() : null);
            } catch (Exception e) {
                log.error("Failed to assign territory to user {}: {}", userId, e.getMessage(), e);
                throw new RuntimeException("Failed to assign territory: " + e.getMessage(), e);
            }
        }
        return createdTerritories;
    }
    
    /**
     * Create UserTerritory entity from request and system entities
     */
    private UserTerritory createUserTerritoryFromRequest(
            User user, 
            UserTerritoryRequestDTO request, 
            GeographicalEntityService.SystemEntitiesResult systemEntities) {
        
        UserTerritory userTerritory = new UserTerritory();
        userTerritory.setUser(user);
        userTerritory.setLevel(request.getTerritoryLevel());
        
        // Set system entities based on territory level
        switch (request.getTerritoryLevel()) {
            case COUNTRY:
                if (systemEntities.getSystemCountry() == null) {
                    throw new IllegalArgumentException("System country is required for COUNTRY territory level");
                }
                userTerritory.setCountry(systemEntities.getSystemCountry());
                break;
                
            case PROVINCE:
                if (systemEntities.getSystemProvince() == null) {
                    throw new IllegalArgumentException("System province is required for PROVINCE territory level");
                }
                userTerritory.setProvince(systemEntities.getSystemProvince());
                userTerritory.setCountry(systemEntities.getSystemCountry());
                break;

            case CITY:
                if (systemEntities.getSystemCity() == null) {
                    throw new IllegalArgumentException("System city is required for CITY territory level");
                }
                userTerritory.setCity(systemEntities.getSystemCity());
                userTerritory.setProvince(systemEntities.getSystemProvince());
                userTerritory.setCountry(systemEntities.getSystemCountry());
                break;

            default:
                throw new IllegalArgumentException("Unsupported territory level: " + request.getTerritoryLevel());
        }
        
        return userTerritory;
    }
    
    /**
     * Get all territories for a specific user
     */
    public List<UserTerritoryResponseDTO> getUserTerritories(Long userId) {
        List<UserTerritory> territories = userTerritoryRepository.findByUser_IdAndIsActiveTrue(userId);
        
        return territories.stream()
                .map(userTerritoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get territories for the current user
     */
    public List<UserTerritoryResponseDTO> getCurrentUserTerritories() {
        List<UserTerritory> territories = geographicalAuthService.getCurrentUserTerritories();
        
        return territories.stream()
                .map(userTerritoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Deactivate a territory assignment
     */
    @Transactional
    public void deactivateTerritory(Long territoryId) {
        UserTerritory territory = userTerritoryRepository.findById(territoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found with id: " + territoryId));
        
        territory.setIsActive(false);
        userTerritoryRepository.save(territory);
    }
    
    /**
     * Get accessible city IDs for the current user
     */
    public List<Long> getAccessibleCities() {
        return geographicalAuthService.getAccessibleCityIds();
    }
    
    /**
     * Check if user has access to a specific location
     */
    public boolean checkLocationAccess(Long cityId, Long provinceId, Long countryId) {
        // Use GeographicalEntityService to resolve system entities (find-only)
        GeographicalEntityService.SystemEntitiesResult systemEntities = geographicalEntityService.findSystemEntities(
            countryId, provinceId, cityId
        );
        return geographicalAuthService.hasAccessToLocation(
            systemEntities.getSystemCity(),
            systemEntities.getSystemProvince(),
            systemEntities.getSystemCountry()
        );
    }
    
    /**
     * Get all territories for a user by level
     */
    public List<UserTerritoryResponseDTO> getUserTerritoriesByLevel(Long userId, TerritoryLevel level) {
        List<UserTerritory> territories = userTerritoryRepository.findByUser_IdAndLevelAndIsActiveTrue(userId, level);
        
        return territories.stream()
                .map(userTerritoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if user has any territory assignments
     */
    public boolean hasTerritoryAssignments(Long userId) {
        User currentUser = geographicalAuthService.getCurrentUser();
        String currentRole = currentUser.getRole().getName();
        if ("PLATFORM_ADMIN".equals(currentRole)) {
            // Admin: check if target user has any territory assignments
            List<UserTerritory> targetTerritories = userTerritoryRepository.findByUser_IdAndIsActiveTrue(userId);
            return !targetTerritories.isEmpty();
        }
        if ("SALES_MANAGER".equals(currentRole)) {
            // Manager: allow if target user has at least one territory in manager's managed area
            List<UserTerritory> targetTerritories = userTerritoryRepository.findByUser_IdAndIsActiveTrue(userId);
            if (targetTerritories.isEmpty()) return false;
            List<Long> managerCityIds = geographicalAuthService.getAccessibleCityIds();
            List<Long> managerProvinceIds = geographicalAuthService.getAccessibleProvinceIds();
            List<Long> managerCountryIds = geographicalAuthService.getAccessibleCountryIds();
            boolean hasOverlap = targetTerritories.stream().anyMatch(ut ->
                (ut.getCity() != null && (managerCityIds.isEmpty() || managerCityIds.contains(ut.getCity().getId()))) ||
                (ut.getProvince() != null && (managerProvinceIds.isEmpty() || managerProvinceIds.contains(ut.getProvince().getId()))) ||
                (ut.getCountry() != null && (managerCountryIds.isEmpty() || managerCountryIds.contains(ut.getCountry().getId())))
            );
            if (!hasOverlap) {
                throw new UnAuthorizedException("Sales manager does not manage any of this user's territories");
            }
            return true;
        }
        // Default: only allow user to check their own assignments
        if (!currentUser.getId().equals(userId)) {
            throw new UnAuthorizedException("You are not authorized to check this user's territory assignments");
        }
        return geographicalAuthService.hasTerritoryAssignments(userId);
    }
    
    /**
     * Get all active territories
     */
    public List<UserTerritoryResponseDTO> getAllActiveTerritories() {
        List<UserTerritory> territories = userTerritoryRepository.findByIsActiveTrue();
        
        return territories.stream()
                .map(userTerritoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
} 