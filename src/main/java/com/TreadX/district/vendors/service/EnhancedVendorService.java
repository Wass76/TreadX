package com.TreadX.district.vendors.service;

import com.TreadX.district.vendors.dto.VendorCreationRequestDTO;
import com.TreadX.district.vendors.dto.VendorCreationResponseDTO;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.plans.dto.VendorSubscriptionRequestDTO;
import com.TreadX.plans.entity.SubscriptionPlan;
import com.TreadX.plans.entity.VendorSubscription;
import com.TreadX.plans.repository.SubscriptionPlanRepository;
import com.TreadX.plans.repository.VendorSubscriptionRepository;
import com.TreadX.user.entity.Role;
import com.TreadX.user.repository.RoleRepository;
import com.TreadX.utils.VendorIdGenerator;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnhancedVendorService {
    
    private final VendorService vendorService;
    private final VendorRepository vendorRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final VendorSubscriptionRepository vendorSubscriptionRepository;
    private final RoleRepository roleRepository;

    public VendorCreationResponseDTO createVendorWithAccessAndSubscription(VendorCreationRequestDTO request) {
        // 1. Create the vendor
        Vendor vendor = createVendor(request);
        
        // 2. Create subscription
        VendorSubscription subscription = createVendorSubscription(vendor, request);
        
        // 3. Create user access records (simplified for now)
        List<VendorCreationResponseDTO.UserAccessInfo> userAccessList = createUserAccessRecords(vendor, request);
        
        // 4. Build response
        return buildVendorCreationResponse(vendor, subscription, request, userAccessList);
    }
    
    private Vendor createVendor(VendorCreationRequestDTO request) {
        // Convert to basic vendor request
        com.TreadX.district.vendors.dto.VendorRequestDTO vendorRequest = new com.TreadX.district.vendors.dto.VendorRequestDTO();
        vendorRequest.setLeadId(request.getLeadId());
        vendorRequest.setLegalName(request.getLegalName());
        vendorRequest.setBusinessName(request.getBusinessName());
        vendorRequest.setStreetNumber(request.getStreetNumber());
        vendorRequest.setStreetName(request.getStreetName());
        vendorRequest.setAptUnitBldg(request.getAptUnitBldg());
        vendorRequest.setPostalCode(request.getPostalCode());
        vendorRequest.setEmail(request.getEmail());
        vendorRequest.setPhoneNumber(request.getPhoneNumber());
        vendorRequest.setStatus(request.getStatus());
        
        // Create vendor using existing service
        com.TreadX.district.vendors.dto.VendorResponseDTO vendorResponse = vendorService.createVendor(vendorRequest);
        
        // Get the created vendor entity
        return vendorRepository.findById(vendorResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found after creation"));
    }
    
    private VendorSubscription createVendorSubscription(Vendor vendor, VendorCreationRequestDTO request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + request.getSubscriptionPlanId()));
        
        // Calculate subscription end date based on billing cycle
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan.getBillingCycle());
        
        VendorSubscription subscription = new VendorSubscription();
        subscription.setVendor(vendor);
        subscription.setSubscriptionPlan(plan);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus(VendorSubscription.SubscriptionStatus.ACTIVE);
        subscription.setAmountPaid(plan.getPrice());
        subscription.setAutoRenew(request.getAutoRenew() != null ? request.getAutoRenew() : true);
        
        return vendorSubscriptionRepository.save(subscription);
    }
    
    private LocalDateTime calculateEndDate(LocalDateTime startDate, SubscriptionPlan.BillingCycle billingCycle) {
        return switch (billingCycle) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case YEARLY -> startDate.plusYears(1);
        };
    }
    
    private List<VendorCreationResponseDTO.UserAccessInfo> createUserAccessRecords(Vendor vendor, VendorCreationRequestDTO request) {
        List<VendorCreationResponseDTO.UserAccessInfo> userAccessList = new ArrayList<>();
        
        if (request.getUserRoles() != null) {
            for (Map.Entry<String, Integer> entry : request.getUserRoles().entrySet()) {
                String role = entry.getKey();
                Integer count = entry.getValue();
                
                for (int i = 0; i < count; i++) {
                    com.TreadX.user.entity.Role roleEntity = roleRepository.findByName(role).orElseThrow(
                            ()-> new ResourceNotFoundException("Role doesn't exist with name: " + role)
                    );
                    VendorCreationResponseDTO.UserAccessInfo userInfo = new VendorCreationResponseDTO.UserAccessInfo();
                    userInfo.setUsername(generateUsername(vendor, roleEntity, i + 1));
                    userInfo.setEmail(generateEmail(vendor, roleEntity, i + 1));
                    userInfo.setRole(role);
                    userInfo.setStatus("PENDING"); // Users need to be activated
                    userAccessList.add(userInfo);
                }
            }
        }
        
        return userAccessList;
    }
    
    private String generateUsername(Vendor vendor, Role role, int userNumber) {
        String baseName = vendor.getBusinessName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        return baseName + "_" + role.getName().toLowerCase() + "_" + userNumber;
    }
    
    private String generateEmail(Vendor vendor, Role role, int userNumber) {
        String baseName = vendor.getBusinessName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        return baseName + "_" + role.getName().toLowerCase() + "_" + userNumber + "@" + baseName + ".com";
    }
    
    private VendorCreationResponseDTO buildVendorCreationResponse(Vendor vendor, VendorSubscription subscription, 
                                                                VendorCreationRequestDTO request, 
                                                                List<VendorCreationResponseDTO.UserAccessInfo> userAccessList) {
        VendorCreationResponseDTO response = new VendorCreationResponseDTO();
        
        // Basic vendor information
        response.setId(vendor.getId());
        response.setLegalName(vendor.getLegalName());
        response.setBusinessName(vendor.getBusinessName());
        response.setStreetNumber(vendor.getStreetNumber());
        response.setStreetName(vendor.getStreetName());
        response.setAptUnitBldg(vendor.getAptUnitBldg());
        response.setPostalCode(vendor.getPostalCode());
        response.setEmail(vendor.getEmail());
        response.setPhoneNumber(vendor.getPhoneNumber());
        response.setVendorStatus(vendor.getVendorStatus());
        response.setVendorUniqueId(vendor.getVendorUniqueId());
        
        // User access information
        response.setTotalUsers(request.getTotalUsers());
        response.setUserRoles(request.getUserRoles());
        response.setUserAccessList(userAccessList);
        
        // Subscription information
        response.setSubscriptionId(subscription.getId());
        response.setPlanName(subscription.getSubscriptionPlan().getPlanName());
        response.setPlanPrice(subscription.getSubscriptionPlan().getPrice());
        response.setBillingCycle(subscription.getSubscriptionPlan().getBillingCycle().name());
        response.setSubscriptionStartDate(subscription.getStartDate());
        response.setSubscriptionEndDate(subscription.getEndDate());
        response.setAutoRenew(subscription.getAutoRenew());
        
        // Timestamps
        response.setCreatedAt(vendor.getCreatedAt());
        response.setUpdatedAt(vendor.getUpdatedAt());
        
        return response;
    }
} 