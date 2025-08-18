package com.TreadX.plans.service;

import com.TreadX.plans.dto.SubscriptionPlanRequestDTO;
import com.TreadX.plans.dto.SubscriptionPlanResponseDTO;
import com.TreadX.plans.entity.SubscriptionPlan;
import com.TreadX.plans.repository.SubscriptionPlanRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionPlanService {
    
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ObjectMapper objectMapper;
    
    public SubscriptionPlanResponseDTO createSubscriptionPlan(SubscriptionPlanRequestDTO request) {
        if (subscriptionPlanRepository.existsByPlanName(request.getPlanName())) {
            throw new IllegalArgumentException("Plan name already exists");
        }
        
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .planName(request.getPlanName())
                .description(request.getDescription())
                .price(request.getPrice())
                .billingCycle(request.getBillingCycle())
                .maxUsers(request.getMaxUsers())
                .maxTireStorage(request.getMaxTireStorage())
                .isActive(request.getIsActive())
                .features(convertFeaturesToJson(request.getFeatures()))
                .build();
        
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return convertToResponseDTO(savedPlan);
    }
    
    public SubscriptionPlanResponseDTO getSubscriptionPlanById(Long id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        return convertToResponseDTO(plan);
    }
    
    public Page<SubscriptionPlanResponseDTO> getAllSubscriptionPlans(Pageable pageable) {
        Page<SubscriptionPlan> plans = subscriptionPlanRepository.findAll(pageable);
        return plans.map(this::convertToResponseDTO);
    }
    
    public Page<SubscriptionPlanResponseDTO> getActiveSubscriptionPlans(Pageable pageable) {
        Page<SubscriptionPlan> plans = subscriptionPlanRepository.findByIsActiveTrue(pageable);
        return plans.map(this::convertToResponseDTO);
    }
    
    public List<SubscriptionPlanResponseDTO> getPlansByUserCount(Integer userCount) {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findActivePlansByUserCount(userCount);
        return plans.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public SubscriptionPlanResponseDTO updateSubscriptionPlan(Long id, SubscriptionPlanRequestDTO request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        
        plan.setPlanName(request.getPlanName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setBillingCycle(request.getBillingCycle());
        plan.setMaxUsers(request.getMaxUsers());
        plan.setMaxTireStorage(request.getMaxTireStorage());
        plan.setIsActive(request.getIsActive());
        plan.setFeatures(convertFeaturesToJson(request.getFeatures()));
        
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return convertToResponseDTO(savedPlan);
    }
    
    public void deleteSubscriptionPlan(Long id) {
        if (!subscriptionPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription plan not found with id: " + id);
        }
        subscriptionPlanRepository.deleteById(id);
    }
    
    private String convertFeaturesToJson(List<String> features) {
        try {
            return objectMapper.writeValueAsString(features);
        } catch (JsonProcessingException e) {
            log.error("Error converting features to JSON", e);
            return "[]";
        }
    }
    
    private List<String> convertJsonToFeatures(String featuresJson) {
        try {
            return objectMapper.readValue(featuresJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to features", e);
            return List.of();
        }
    }
    
    private SubscriptionPlanResponseDTO convertToResponseDTO(SubscriptionPlan plan) {
        SubscriptionPlanResponseDTO dto = new SubscriptionPlanResponseDTO();
        dto.setId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setBillingCycle(plan.getBillingCycle());
        dto.setMaxUsers(plan.getMaxUsers());
        dto.setMaxTireStorage(plan.getMaxTireStorage());
        dto.setIsActive(plan.getIsActive());
        dto.setFeatures(convertJsonToFeatures(plan.getFeatures()));
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        return dto;
    }
} 