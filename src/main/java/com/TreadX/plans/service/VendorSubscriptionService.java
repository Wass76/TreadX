package com.TreadX.plans.service;

import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.plans.dto.VendorSubscriptionRequestDTO;
import com.TreadX.plans.dto.VendorSubscriptionResponseDTO;
import com.TreadX.plans.entity.SubscriptionPlan;
import com.TreadX.plans.entity.VendorSubscription;
import com.TreadX.plans.repository.SubscriptionPlanRepository;
import com.TreadX.plans.repository.VendorSubscriptionRepository;
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
public class VendorSubscriptionService {
    
    private final VendorSubscriptionRepository vendorSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final VendorRepository vendorRepository;
    
    public VendorSubscriptionResponseDTO createVendorSubscription(VendorSubscriptionRequestDTO request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + request.getVendorId()));
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + request.getSubscriptionPlanId()));
        
        // Check if vendor already has an active subscription
        vendorSubscriptionRepository.findActiveSubscriptionByVendorId(request.getVendorId())
                .ifPresent(existing -> {
                    throw new IllegalStateException("Vendor already has an active subscription");
                });
        
        VendorSubscription subscription = VendorSubscription.builder()
                .vendor(vendor)
                .subscriptionPlan(plan)
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now())
                .endDate(request.getEndDate())
                .status(VendorSubscription.SubscriptionStatus.ACTIVE)
                .amountPaid(request.getAmountPaid())
                .autoRenew(request.getAutoRenew() != null ? request.getAutoRenew() : true)
                .build();
        
        VendorSubscription savedSubscription = vendorSubscriptionRepository.save(subscription);
        return convertToResponseDTO(savedSubscription);
    }
    
    public VendorSubscriptionResponseDTO getVendorSubscriptionById(Long id) {
        VendorSubscription subscription = vendorSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor subscription not found with id: " + id));
        return convertToResponseDTO(subscription);
    }
    
    public Page<VendorSubscriptionResponseDTO> getAllVendorSubscriptions(Pageable pageable) {
        Page<VendorSubscription> subscriptions = vendorSubscriptionRepository.findAll(pageable);
        return subscriptions.map(this::convertToResponseDTO);
    }
    
    public List<VendorSubscriptionResponseDTO> getVendorSubscriptionsByVendorId(Long vendorId) {
        List<VendorSubscription> subscriptions = vendorSubscriptionRepository.findByVendorId(vendorId);
        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public VendorSubscriptionResponseDTO getActiveVendorSubscription(Long vendorId) {
        VendorSubscription subscription = vendorSubscriptionRepository.findActiveSubscriptionByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for vendor: " + vendorId));
        return convertToResponseDTO(subscription);
    }
    
    public VendorSubscriptionResponseDTO cancelVendorSubscription(Long subscriptionId, String reason) {
        VendorSubscription subscription = vendorSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor subscription not found with id: " + subscriptionId));
        
        subscription.setStatus(VendorSubscription.SubscriptionStatus.CANCELLED);
        subscription.setCancellationDate(LocalDateTime.now());
        subscription.setCancellationReason(reason);
        
        VendorSubscription savedSubscription = vendorSubscriptionRepository.save(subscription);
        return convertToResponseDTO(savedSubscription);
    }
    
    public VendorSubscriptionResponseDTO updateVendorSubscription(Long id, VendorSubscriptionRequestDTO request) {
        VendorSubscription subscription = vendorSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor subscription not found with id: " + id));
        
        if (request.getSubscriptionPlanId() != null) {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getSubscriptionPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + request.getSubscriptionPlanId()));
            subscription.setSubscriptionPlan(plan);
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
        
        VendorSubscription savedSubscription = vendorSubscriptionRepository.save(subscription);
        return convertToResponseDTO(savedSubscription);
    }
    
    public void deleteVendorSubscription(Long id) {
        if (!vendorSubscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vendor subscription not found with id: " + id);
        }
        vendorSubscriptionRepository.deleteById(id);
    }
    
    private VendorSubscriptionResponseDTO convertToResponseDTO(VendorSubscription subscription) {
        VendorSubscriptionResponseDTO dto = new VendorSubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setVendorId(subscription.getVendor().getId());
        dto.setVendorName(subscription.getVendor().getBusinessName());
        dto.setSubscriptionPlanId(subscription.getSubscriptionPlan().getId());
        dto.setPlanName(subscription.getSubscriptionPlan().getPlanName());
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