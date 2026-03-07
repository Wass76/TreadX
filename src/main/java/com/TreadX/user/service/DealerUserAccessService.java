package com.TreadX.user.service;

import com.TreadX.district.dealer.dto.DealerCreationResponseDTO;
import com.TreadX.district.dealer.entity.Dealer;
import com.TreadX.user.entity.Role;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.DealerStaff;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.DealerStaffRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.TreadX.user.config.RoleConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealerUserAccessService {
    
    private final UserRepository userRepository;
    private final DealerStaffRepository dealerStaffRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<DealerStaff> createDealerUsers(Dealer dealer, Map<Role, Integer> userRoles, String districtCode) {
        List<DealerStaff> createdUsers = new ArrayList<>();
        
        for (Map.Entry<Role, Integer> entry : userRoles.entrySet()) {
            Role role = entry.getKey();
            Integer count = entry.getValue();
            
            for (int i = 0; i < count; i++) {
                DealerStaff dealerStaff = createDealerUser(dealer, role, districtCode, i + 1);
                createdUsers.add(dealerStaff);
            }
        }
        
        return createdUsers;
    }
    
    private DealerStaff createDealerUser(Dealer dealer, Role role, String districtCode, int userNumber) {
        // Generate email
        String baseName = dealer.getBusinessName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        String email = baseName + "_" + role.getName().toLowerCase() + "_" + userNumber + "@" + baseName + ".com";
        
        // Create user
        User user = User.builder()
                .firstName("Dealer")
                .lastName("User")
                .email(email)
                .password(passwordEncoder.encode(generateDefaultPassword()))
                .role(role)
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create dealer staff record
        DealerStaff dealerStaff = new DealerStaff();
        dealerStaff.setUser(savedUser);
        dealerStaff.setDealerId(dealer.getId());
        dealerStaff.setDistrictCode(districtCode);
        dealerStaff.setAccessLevel(mapRoleToAccessLevel(role));
        
        return dealerStaffRepository.save(dealerStaff);
    }
    
    private String generateDefaultPassword() {
        // Generate a random password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    private DealerStaff.DealerAccessLevel mapRoleToAccessLevel(Role role) {
        switch (role.getName()) {
            case DEALER_ADMIN:
                return DealerStaff.DealerAccessLevel.OWNER;
            case DEALER_EMPLOYEE:
                return DealerStaff.DealerAccessLevel.MANAGER;
            case DEALER_TECHNICIAN:
                return DealerStaff.DealerAccessLevel.MECHANIC;
            default:
                return DealerStaff.DealerAccessLevel.VIEWER;
        }
    }
    
    public List<DealerStaff> getDealerUsers(Long dealerId) {
        return dealerStaffRepository.findByDealerIdAndDistrictCode(dealerId, "DEFAULT");
    }
    
    public DealerStaff updateDealerUserAccess(Long dealerStaffId, DealerStaff.DealerAccessLevel newAccessLevel) {
        DealerStaff dealerStaff = dealerStaffRepository.findById(dealerStaffId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer staff not found with id: " + dealerStaffId));
        
        dealerStaff.setAccessLevel(newAccessLevel);
        return dealerStaffRepository.save(dealerStaff);
    }
    
    public void deactivateDealerUser(Long dealerStaffId) {
        DealerStaff dealerStaff = dealerStaffRepository.findById(dealerStaffId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer staff not found with id: " + dealerStaffId));
        
        User user = dealerStaff.getUser();
        user.setActive(false);
        userRepository.save(user);
    }
    
    public Map<Role, Integer> getUserRoleCounts(Long dealerId) {
        List<DealerStaff> dealerStaffList = dealerStaffRepository.findByDealerIdAndDistrictCode(dealerId, "DEFAULT");
        
        return dealerStaffList.stream()
                .filter(vs -> vs.getUser().isActive())
                .collect(Collectors.groupingBy(
                    vs -> vs.getUser().getRole(),
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
    
    public List<DealerCreationResponseDTO.UserAccessInfo> getUserAccessInfo(Long dealerId) {
        List<DealerStaff> dealerStaffList = dealerStaffRepository.findByDealerIdAndDistrictCode(dealerId, "DEFAULT");
        
        return dealerStaffList.stream()
                .map(this::convertToUserAccessInfo)
                .collect(Collectors.toList());
    }
    
    private DealerCreationResponseDTO.UserAccessInfo convertToUserAccessInfo(DealerStaff dealerStaff) {
        DealerCreationResponseDTO.UserAccessInfo info = new DealerCreationResponseDTO.UserAccessInfo();
        info.setUsername(dealerStaff.getUser().getEmail()); // Use email as username
        info.setEmail(dealerStaff.getUser().getEmail());
        info.setRole(dealerStaff.getUser().getRole().getName());
        info.setStatus(dealerStaff.getUser().isActive() ? "ACTIVE" : "INACTIVE");
        return info;
    }
} 