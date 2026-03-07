package com.TreadX.plans.service;

import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.repository.DealerRepository;
import com.TreadX.plans.dto.SubscriptionRequestDTO;
import com.TreadX.plans.dto.SubscriptionResponseDTO;
import com.TreadX.plans.entity.Plan;
import com.TreadX.plans.entity.Subscription;
import com.TreadX.plans.repository.PlanRepository;
import com.TreadX.plans.repository.SubscriptionRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final DealerRepository dealerRepository;
    
    public SubscriptionResponseDTO createSubscription(SubscriptionRequestDTO request) {
        Dealer dealer = dealerRepository.findById(request.getDealerId())
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getDealerId()));
        
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));
        
        // Check if vendor already has an active subscription
        subscriptionRepository.findActiveSubscriptionByDealerId(request.getDealerId())
                .ifPresent(existing -> {
                    throw new IllegalStateException("Dealer already has an active subscription");
                });
        
        Subscription subscription = Subscription.builder()
                .dealer(dealer)
                .plan(plan)
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now())
                .endDate(request.getEndDate())
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .amountPaid(request.getAmountPaid())
                .autoRenew(request.getAutoRenew() != null ? request.getAutoRenew() : true)
                .build();
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToResponseDTO(savedSubscription);
    }
    
    public SubscriptionResponseDTO getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer subscription not found with id: " + id));
        return convertToResponseDTO(subscription);
    }
    
    public Page<SubscriptionResponseDTO> getAllSubscriptions(Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findAll(pageable);
        return subscriptions.map(this::convertToResponseDTO);
    }
    
    public List<SubscriptionResponseDTO> getSubscriptionsByDealerId(Long dealerId) {
        List<Subscription> subscriptions = subscriptionRepository.findByDealerId(dealerId);
        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public SubscriptionResponseDTO getActiveSubscription(Long dealerId) {
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByDealerId(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for dealer: " + dealerId));
        return convertToResponseDTO(subscription);
    }
    
    public SubscriptionResponseDTO cancelSubscription(Long subscriptionId, String reason) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));
        
        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscription.setCancellationDate(LocalDateTime.now());
        subscription.setCancellationReason(reason);
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToResponseDTO(savedSubscription);
    }
    
    public SubscriptionResponseDTO updateSubscription(Long id, SubscriptionRequestDTO request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer subscription not found with id: " + id));
        
        if (request.getPlanId() != null) {
            Plan plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));
            subscription.setPlan(plan);
        }
        
        if (request.getStartDate() != null) {
            subscription.setStartDate(request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            subscription.setEndDate(request.getEndDate());
        }
        
        if (request.getAmountPaid() != null) {
            subscription.setAmountPaid(request.getAmountPaid());
        }
        
        if (request.getAutoRenew() != null) {
            subscription.setAutoRenew(request.getAutoRenew());
        }
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToResponseDTO(savedSubscription);
    }
    
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }
    
    private SubscriptionResponseDTO convertToResponseDTO(Subscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setDealerId(subscription.getDealer().getId());
        dto.setDealerName(subscription.getDealer().getBusinessName());
        dto.setPlanId(subscription.getPlan().getId());
        dto.setPlanName(subscription.getPlan().getPlanName());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setStatus(subscription.getStatus());
        dto.setAmountPaid(subscription.getAmountPaid());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setCancellationDate(subscription.getCancellationDate());
        dto.setCancellationReason(subscription.getCancellationReason());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        return dto;
    }
} 