package com.TreadX.user.service;

import com.TreadX.district.vendors.dto.VendorCreationResponseDTO;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.user.entity.Role;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.VendorStaff;
import com.TreadX.user.repository.UserRepository;
import com.TreadX.user.repository.VendorStaffRepository;
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
public class VendorUserAccessService {
    
    private final UserRepository userRepository;
    private final VendorStaffRepository vendorStaffRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<VendorStaff> createVendorUsers(Vendor vendor, Map<Role, Integer> userRoles, String districtCode) {
        List<VendorStaff> createdUsers = new ArrayList<>();
        
        for (Map.Entry<Role, Integer> entry : userRoles.entrySet()) {
            Role role = entry.getKey();
            Integer count = entry.getValue();
            
            for (int i = 0; i < count; i++) {
                VendorStaff vendorStaff = createVendorUser(vendor, role, districtCode, i + 1);
                createdUsers.add(vendorStaff);
            }
        }
        
        return createdUsers;
    }
    
    private VendorStaff createVendorUser(Vendor vendor, Role role, String districtCode, int userNumber) {
        // Generate email
        String baseName = vendor.getBusinessName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        String email = baseName + "_" + role.getName().toLowerCase() + "_" + userNumber + "@" + baseName + ".com";
        
        // Create user
        User user = User.builder()
                .firstName("Vendor")
                .lastName("User")
                .email(email)
                .password(passwordEncoder.encode(generateDefaultPassword()))
                .role(role)
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create vendor staff record
        VendorStaff vendorStaff = new VendorStaff();
        vendorStaff.setUser(savedUser);
        vendorStaff.setVendorId(vendor.getId());
        vendorStaff.setDistrictCode(districtCode);
        vendorStaff.setAccessLevel(mapRoleToAccessLevel(role));
        
        return vendorStaffRepository.save(vendorStaff);
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
    
    private VendorStaff.VendorAccessLevel mapRoleToAccessLevel(Role role) {
        switch (role.getName()) {
            case VENDOR_ADMIN:
                return VendorStaff.VendorAccessLevel.OWNER;
            case VENDOR_EMPLOYEE:
                return VendorStaff.VendorAccessLevel.MANAGER;
            case VENDOR_TECHNICIAN:
                return VendorStaff.VendorAccessLevel.MECHANIC;
            default:
                return VendorStaff.VendorAccessLevel.VIEWER;
        }
    }
    
    public List<VendorStaff> getVendorUsers(Long vendorId) {
        return vendorStaffRepository.findByVendorIdAndDistrictCode(vendorId, "DEFAULT");
    }
    
    public VendorStaff updateVendorUserAccess(Long vendorStaffId, VendorStaff.VendorAccessLevel newAccessLevel) {
        VendorStaff vendorStaff = vendorStaffRepository.findById(vendorStaffId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor staff not found with id: " + vendorStaffId));
        
        vendorStaff.setAccessLevel(newAccessLevel);
        return vendorStaffRepository.save(vendorStaff);
    }
    
    public void deactivateVendorUser(Long vendorStaffId) {
        VendorStaff vendorStaff = vendorStaffRepository.findById(vendorStaffId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor staff not found with id: " + vendorStaffId));
        
        User user = vendorStaff.getUser();
        user.setActive(false);
        userRepository.save(user);
    }
    
    public Map<Role, Integer> getUserRoleCounts(Long vendorId) {
        List<VendorStaff> vendorStaffList = vendorStaffRepository.findByVendorIdAndDistrictCode(vendorId, "DEFAULT");
        
        return vendorStaffList.stream()
                .filter(vs -> vs.getUser().isActive())
                .collect(Collectors.groupingBy(
                    vs -> vs.getUser().getRole(),
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
    
    public List<VendorCreationResponseDTO.UserAccessInfo> getUserAccessInfo(Long vendorId) {
        List<VendorStaff> vendorStaffList = vendorStaffRepository.findByVendorIdAndDistrictCode(vendorId, "DEFAULT");
        
        return vendorStaffList.stream()
                .map(this::convertToUserAccessInfo)
                .collect(Collectors.toList());
    }
    
    private VendorCreationResponseDTO.UserAccessInfo convertToUserAccessInfo(VendorStaff vendorStaff) {
        VendorCreationResponseDTO.UserAccessInfo info = new VendorCreationResponseDTO.UserAccessInfo();
        info.setUsername(vendorStaff.getUser().getEmail()); // Use email as username
        info.setEmail(vendorStaff.getUser().getEmail());
        info.setRole(vendorStaff.getUser().getRole().getName());
        info.setStatus(vendorStaff.getUser().isActive() ? "ACTIVE" : "INACTIVE");
        return info;
    }
} 