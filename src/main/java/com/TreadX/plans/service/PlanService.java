package com.TreadX.plans.service;

import com.TreadX.plans.dto.PlanRequestDTO;
import com.TreadX.plans.dto.PlanResponseDTO;
import com.TreadX.plans.entity.Plan;
import com.TreadX.plans.repository.PlanRepository;
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
public class PlanService {
    
    private final PlanRepository planRepository;
    private final ObjectMapper objectMapper;
    
    public PlanResponseDTO createPlan(PlanRequestDTO request) {
        if (planRepository.existsByPlanName(request.getPlanName())) {
            throw new IllegalArgumentException("Plan name already exists");
        }
        
        Plan plan = Plan.builder()
                .planName(request.getPlanName())
                .description(request.getDescription())
                .price(request.getPrice())
                .billingCycle(request.getBillingCycle())
                .maxUsers(request.getMaxUsers())
                .maxTireStorage(request.getMaxTireStorage())
                .isActive(request.getIsActive())
                .features(convertFeaturesToJson(request.getFeatures()))
                .build();
        
        Plan savedPlan = planRepository.save(plan);
        return convertToResponseDTO(savedPlan);
    }
    
    public PlanResponseDTO getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        return convertToResponseDTO(plan);
    }
    
    public Page<PlanResponseDTO> getAllPlans(Pageable pageable) {
        Page<Plan> plans = planRepository.findAll(pageable);
        return plans.map(this::convertToResponseDTO);
    }
    
    public Page<PlanResponseDTO> getActivePlans(Pageable pageable) {
        Page<Plan> plans = planRepository.findByIsActiveTrue(pageable);
        return plans.map(this::convertToResponseDTO);
    }
    
    public List<PlanResponseDTO> getPlansByUserCount(Integer userCount) {
        List<Plan> plans = planRepository.findActivePlansByUserCount(userCount);
        return plans.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public PlanResponseDTO updatePlan(Long id, PlanRequestDTO request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        
        plan.setPlanName(request.getPlanName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setBillingCycle(request.getBillingCycle());
        plan.setMaxUsers(request.getMaxUsers());
        plan.setMaxTireStorage(request.getMaxTireStorage());
        plan.setIsActive(request.getIsActive());
        plan.setFeatures(convertFeaturesToJson(request.getFeatures()));
        
        Plan savedPlan = planRepository.save(plan);
        return convertToResponseDTO(savedPlan);
    }
    
    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan not found with id: " + id);
        }
        planRepository.deleteById(id);
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
    
    private PlanResponseDTO convertToResponseDTO(Plan plan) {
        PlanResponseDTO dto = new PlanResponseDTO();
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