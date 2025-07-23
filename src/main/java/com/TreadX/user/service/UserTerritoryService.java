package com.TreadX.user.service;

import com.TreadX.user.dto.UserTerritoryRequestDTO;
import com.TreadX.user.dto.UserTerritoryResponseDTO;
import com.TreadX.user.entity.Territory;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.UserTerritory;
import com.TreadX.user.mapper.UserTerritoryMapper;
import com.TreadX.user.repository.TerritoryRepository;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.UserTerritoryRepository;
import com.TreadX.utils.exception.ConflictException;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.TreadX.user.Enum.TerritoryLevel;

@Service
@RequiredArgsConstructor
public class UserTerritoryService {
    private static final Logger log = LoggerFactory.getLogger(UserTerritoryService.class);

    private final UserTerritoryRepository userTerritoryRepository;
    private final UserRepository userRepository;
    private final TerritoryRepository territoryRepository;
    private final UserTerritoryMapper userTerritoryMapper;
    private final AuthorizationService authorizationService;

    /**
     * Assign territories to a user by territory IDs
     */
    @Transactional
    public List<UserTerritory> assignTerritoriesToUser(Long userId, List<UserTerritoryRequestDTO> territoryRequests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<UserTerritory> createdTerritories = new ArrayList<>();
        for (UserTerritoryRequestDTO request : territoryRequests) {
            Territory territory = territoryRepository.findById(request.getTerritoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Territory not found with id: " + request.getTerritoryId()));
            UserTerritory userTerritory = new UserTerritory();
            userTerritory.setUser(user);
            userTerritory.setTerritory(territory);
            userTerritory.setIsActive(true);
            UserTerritory savedTerritory = userTerritoryRepository.save(userTerritory);
            createdTerritories.add(savedTerritory);
            log.info("Assigned territory {} to user {}", territory.getCode(), userId);
        }
        return createdTerritories;
    }

    /**
     * Get all territories for a specific user
     */
    public List<UserTerritoryResponseDTO> getUserTerritories(Long userId) {
        if(userId == null){
            userId = getCurrentAuthenticatedUserId();
        }
        List<UserTerritory> territories = userTerritoryRepository.findByUser_IdAndIsActiveTrue(userId);
        return territories.stream()
                .map(userTerritoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all territories for a user by level
     */
    public List<UserTerritoryResponseDTO> getUserTerritoriesByLevel(Long userId, TerritoryLevel level) {
        Long actualUserId = userId != null ? userId : getCurrentAuthenticatedUserId();
        List<UserTerritory> assignments = userTerritoryRepository.findByUser_IdAndIsActiveTrue(actualUserId)
            .stream()
            .filter(ut -> ut.getTerritory().getLevel() == level)
            .collect(Collectors.toList());
        return assignments.stream()
            .map(userTerritoryMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Check if user has any territory assignments
     */
    public boolean hasTerritoryAssignments(Long userId) {
        Long actualUserId = userId != null ? userId : getCurrentAuthenticatedUserId();
        return !userTerritoryRepository.findByUser_IdAndIsActiveTrue(actualUserId).isEmpty();
    }

    /**
     * Deactivate a territory assignment
     */
    @Transactional
    public void deactivateTerritory(Long userTerritoryId) {
        UserTerritory userTerritory = userTerritoryRepository.findById(userTerritoryId)
                .orElseThrow(() -> new ResourceNotFoundException("UserTerritory not found with id: " + userTerritoryId));
        userTerritory.setIsActive(false);
        userTerritoryRepository.save(userTerritory);
    }

    /**
     * Check if user has access to a territory or any of its descendants (hierarchy-aware)
     */
    public boolean hasAccessToTerritory(Long userId, Long territoryId) {
        Long actualUserId = userId != null ? userId : getCurrentAuthenticatedUserId();
        List<UserTerritory> assignments = userTerritoryRepository.findByUser_IdAndIsActiveTrue(actualUserId);
        Territory target = territoryRepository.findById(territoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Territory not found with id: " + territoryId));
        for (UserTerritory ut : assignments) {
            if (isAncestorOrSelf(ut.getTerritory(), target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all territories accessible to a user (descendants of assigned territories)
     */
    public List<Territory> getAllAccessibleTerritories(Long userId) {
        Long actualUserId = userId != null ? userId : getCurrentAuthenticatedUserId();
        List<UserTerritory> assignments = userTerritoryRepository.findByUser_IdAndIsActiveTrue(actualUserId);
        List<Territory> allTerritories = territoryRepository.findByIsActiveTrue();
        List<Territory> accessible = new ArrayList<>();
        for (Territory t : allTerritories) {
            for (UserTerritory ut : assignments) {
                if (isAncestorOrSelf(ut.getTerritory(), t)) {
                    accessible.add(t);
                    break;
                }
            }
        }
        return accessible;
    }

    /**
     * Helper: checks if ancestor is an ancestor (or self) of descendant
     */
    private boolean isAncestorOrSelf(Territory ancestor, Territory descendant) {
        Territory current = descendant;
        while (current != null) {
            if (current.getId().equals(ancestor.getId())) {
                return true;
            }
            if (current.getParentTerritoryCode() == null) break;
            current = territoryRepository.findByCode(current.getParentTerritoryCode()).orElse(null);
        }
        return false;
    }

    /**
     * Get all active user-territory assignments
     */
    public List<UserTerritoryResponseDTO> getAllActiveTerritories() {
        List<UserTerritory> territories = userTerritoryRepository.findByIsActiveTrue();
        return territories.stream()
                .map(userTerritoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a territory by its ID
     */
    public Territory getTerritoryById(Long territoryId) {
        return territoryRepository.findById(territoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Territory not found with id: " + territoryId));
    }

    /**
     * Helper to get current authenticated user's ID
     */
    public Long getCurrentAuthenticatedUserId() {
        // TODO: Implement this using your security context
        // Example for Spring Security:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        // return userDetails.getId();
        Long userId= authorizationService.getCurrentUser() != null ? authorizationService.getCurrentUser().getId() : null;
        if(userId == null) {
            throw new ConflictException("User not found");
        }
        return userId;
//        throw new UnsupportedOperationException("Implement getCurrentAuthenticatedUserId() based on your security context");
    }
} 