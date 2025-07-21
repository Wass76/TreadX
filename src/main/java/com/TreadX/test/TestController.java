package com.TreadX.test;

import com.TreadX.security.SecurityContextService;
import com.TreadX.user.entity.User;
import com.TreadX.user.entity.UserTerritoryAccess;
import com.TreadX.user.entity.VendorStaff;
import com.TreadX.user.repository.UserTerritoryAccessRepository;
import com.TreadX.user.repository.VendorStaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final SecurityContextService securityContext;
    private final UserTerritoryAccessRepository userTerritoryAccessRepository;
    private final VendorStaffRepository vendorStaffRepository;

    @GetMapping("/current-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo() {
        try {
            User currentUser = securityContext.getCurrentUser();
            List<String> accessibleTerritories = securityContext.getAccessibleTerritories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", currentUser.getId());
            response.put("email", currentUser.getEmail());
            response.put("firstName", currentUser.getFirstName());
            response.put("lastName", currentUser.getLastName());
            response.put("accessibleTerritories", accessibleTerritories);
            response.put("isPlatformAdmin", securityContext.isPlatformAdmin());
            response.put("isSalesManager", securityContext.isSalesManager());
            response.put("isSalesAgent", securityContext.isSalesAgent());
            response.put("isVendorStaff", securityContext.isVendorStaff());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/territory-access/{territoryCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> testTerritoryAccess(@PathVariable String territoryCode) {
        try {
            boolean canAccess = securityContext.canAccessTerritory(territoryCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("territoryCode", territoryCode);
            response.put("canAccess", canAccess);
            response.put("accessibleTerritories", securityContext.getAccessibleTerritories());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/vendor-access/{vendorId}/{districtCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> testVendorAccess(
            @PathVariable Long vendorId,
            @PathVariable String districtCode) {
        try {
            boolean canRead = securityContext.canAccessVendorData(vendorId, districtCode, "READ");
            boolean canWrite = securityContext.canAccessVendorData(vendorId, districtCode, "WRITE");
            VendorStaff vendorStaff = securityContext.getVendorStaffInfo();
            
            Map<String, Object> response = new HashMap<>();
            response.put("vendorId", vendorId);
            response.put("districtCode", districtCode);
            response.put("canRead", canRead);
            response.put("canWrite", canWrite);
            response.put("vendorStaffInfo", vendorStaff);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/primary-territory")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getPrimaryTerritory() {
        try {
            String primaryTerritory = securityContext.getPrimaryTerritory();
            
            Map<String, Object> response = new HashMap<>();
            response.put("primaryTerritory", primaryTerritory);
            response.put("accessibleTerritories", securityContext.getAccessibleTerritories());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("accessibleTerritories", securityContext.getAccessibleTerritories());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/all-territory-access")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<UserTerritoryAccess>> getAllTerritoryAccess() {
        List<UserTerritoryAccess> allAccess = userTerritoryAccessRepository.findAll();
        return ResponseEntity.ok(allAccess);
    }

    @GetMapping("/all-vendor-staff")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<VendorStaff>> getAllVendorStaff() {
        List<VendorStaff> allStaff = vendorStaffRepository.findAll();
        return ResponseEntity.ok(allStaff);
    }
} 