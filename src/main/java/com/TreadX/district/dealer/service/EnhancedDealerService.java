package com.TreadX.district.dealer.service;

import com.TreadX.district.dealer.dto.DealerCreationRequestDTO;
import com.TreadX.district.dealer.dto.DealerCreationResponseDTO;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.district.dealer.repository.DealerRepository;
import com.TreadX.plans.entity.Plan;
import com.TreadX.plans.entity.Subscription;
import com.TreadX.plans.repository.PlanRepository;
import com.TreadX.plans.repository.SubscriptionRepository;
import com.TreadX.user.entity.Role;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnhancedDealerService {
    
    private final DealerService dealerService;
    private final DealerRepository dealerRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RoleRepository roleRepository;

    public DealerCreationResponseDTO createDealerWithAccessAndSubscription(DealerCreationRequestDTO request) {
        // 1. Create the dealer
        Dealer dealer = createDealer(request);
        
        // 2. Create subscription
        Subscription subscription = createDealerSubscription(dealer, request);
        
        // 3. Create user access records (simplified for now)
        List<DealerCreationResponseDTO.UserAccessInfo> userAccessList = createUserAccessRecords(dealer, request);
        
        // 4. Build response
        return buildDealerCreationResponse(dealer, subscription, request, userAccessList);
    }
    
    private Dealer createDealer(DealerCreationRequestDTO request) {
        // Convert to basic dealer request
        com.TreadX.district.dealer.dto.DealerRequestDTO dealerRequest = new com.TreadX.district.dealer.dto.DealerRequestDTO();
        dealerRequest.setLeadId(request.getLeadId());
        dealerRequest.setLegalName(request.getLegalName());
        dealerRequest.setBusinessName(request.getBusinessName());
        dealerRequest.setStreetNumber(request.getStreetNumber());
        dealerRequest.setStreetName(request.getStreetName());
        dealerRequest.setAptUnitBldg(request.getAptUnitBldg());
        dealerRequest.setPostalCode(request.getPostalCode());
        dealerRequest.setEmail(request.getEmail());
        dealerRequest.setPhoneNumber(request.getPhoneNumber());
        dealerRequest.setStatus(request.getStatus());
        
        // Create vendor using existing service
        com.TreadX.district.dealer.dto.DealerResponseDTO dealerResponse = dealerService.createDealer(dealerRequest);
        
        // Get the created vendor entity
        return dealerRepository.findById(dealerResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found after creation"));
    }
    
    private Subscription createDealerSubscription(Dealer dealer, DealerCreationRequestDTO request) {
        Plan plan = planRepository.findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + request.getSubscriptionPlanId()));
        
        // Calculate subscription end date based on billing cycle
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan.getBillingCycle());
        
        Subscription subscription = new Subscription();
        subscription.setDealer(dealer);
        subscription.setPlan(plan);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription.setAmountPaid(plan.getPrice());
        subscription.setAutoRenew(request.getAutoRenew() != null ? request.getAutoRenew() : true);
        
        return subscriptionRepository.save(subscription);
    }
    
    private LocalDateTime calculateEndDate(LocalDateTime startDate, Plan.BillingCycle billingCycle) {
        return switch (billingCycle) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case YEARLY -> startDate.plusYears(1);
        };
    }
    
    private List<DealerCreationResponseDTO.UserAccessInfo> createUserAccessRecords(Dealer dealer, DealerCreationRequestDTO request) {
        List<DealerCreationResponseDTO.UserAccessInfo> userAccessList = new ArrayList<>();
        
        if (request.getUserRoles() != null) {
            for (Map.Entry<String, Integer> entry : request.getUserRoles().entrySet()) {
                String role = entry.getKey();
                Integer count = entry.getValue();
                
                for (int i = 0; i < count; i++) {
                    com.TreadX.user.entity.Role roleEntity = roleRepository.findByName(role).orElseThrow(
                            ()-> new ResourceNotFoundException("Role doesn't exist with name: " + role)
                    );
                    DealerCreationResponseDTO.UserAccessInfo userInfo = new DealerCreationResponseDTO.UserAccessInfo();
                    userInfo.setUsername(generateUsername(dealer, roleEntity, i + 1));
                    userInfo.setEmail(generateEmail(dealer, roleEntity, i + 1));
                    userInfo.setRole(role);
                    userInfo.setStatus("PENDING"); // Users need to be activated
                    userAccessList.add(userInfo);
                }
            }
        }
        
        return userAccessList;
    }
    
    private String generateUsername(Dealer dealer, Role role, int userNumber) {
        String baseName = dealer.getBusinessName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        return baseName + "_" + role.getName().toLowerCase() + "_" + userNumber;
    }
    
    private String generateEmail(Dealer dealer, Role role, int userNumber) {
        String baseName = dealer.getBusinessName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        return baseName + "_" + role.getName().toLowerCase() + "_" + userNumber + "@" + baseName + ".com";
    }
    
    private DealerCreationResponseDTO buildDealerCreationResponse(Dealer dealer, Subscription subscription, 
                                                                DealerCreationRequestDTO request, 
                                                                List<DealerCreationResponseDTO.UserAccessInfo> userAccessList) {
        DealerCreationResponseDTO response = new DealerCreationResponseDTO();
        
        // Basic vendor information
        response.setId(dealer.getId());
        response.setLegalName(dealer.getLegalName());
        response.setBusinessName(dealer.getBusinessName());
        response.setStreetNumber(dealer.getStreetNumber());
        response.setStreetName(dealer.getStreetName());
        response.setAptUnitBldg(dealer.getAptUnitBldg());
        response.setPostalCode(dealer.getPostalCode());
        response.setEmail(dealer.getEmail());
        response.setPhoneNumber(dealer.getPhoneNumber());
        response.setDealerStatus(dealer.getDealerStatus());
        response.setDealerUniqueId(dealer.getDealerUniqueId());
        
        // User access information
        response.setTotalUsers(request.getTotalUsers());
        response.setUserRoles(request.getUserRoles());
        response.setUserAccessList(userAccessList);
        
        // Subscription information
        response.setSubscriptionId(subscription.getId());
        response.setPlanName(subscription.getPlan().getPlanName());
        response.setPlanPrice(subscription.getPlan().getPrice());
        response.setBillingCycle(subscription.getPlan().getBillingCycle().name());
        response.setSubscriptionStartDate(subscription.getStartDate());
        response.setSubscriptionEndDate(subscription.getEndDate());
        response.setAutoRenew(subscription.getAutoRenew());
        
        // Timestamps
        response.setCreatedAt(dealer.getCreatedAt());
        response.setUpdatedAt(dealer.getUpdatedAt());
        
        return response;
    }
} 