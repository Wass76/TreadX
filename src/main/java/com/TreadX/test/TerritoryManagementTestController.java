//package com.TreadX.test;
//
//import com.TreadX.config.TerritoryDataSourceLookup;
//import com.TreadX.user.dto.TerritoryResponseDTO;
//import com.TreadX.user.Enum.TerritoryLevel;
//import com.TreadX.user.service.TerritoryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/test/territory-management")
//@RequiredArgsConstructor
//public class TerritoryManagementTestController {
//
//    private final TerritoryService territoryService;
//    private final TerritoryDataSourceLookup territoryDataSourceLookup;
//
//    /**
//     * Test territory service - get all territories
//     */
//    @GetMapping("/territories")
//    public ResponseEntity<List<TerritoryResponseDTO>> getAllTerritories() {
//        List<TerritoryResponseDTO> territories = territoryService.getAllActiveTerritories();
//        return ResponseEntity.ok(territories);
//    }
//
//    /**
//     * Test territory service - get territories by level
//     */
//    @GetMapping("/territories/level/{level}")
//    public ResponseEntity<List<TerritoryResponseDTO>> getTerritoriesByLevel(@PathVariable TerritoryLevel level) {
//        List<TerritoryResponseDTO> territories = territoryService.getTerritoriesByLevel(level);
//        return ResponseEntity.ok(territories);
//    }
//
//    /**
//     * Test territory service - get territory by code
//     */
//    @GetMapping("/territories/{code}")
//    public ResponseEntity<TerritoryResponseDTO> getTerritoryByCode(@PathVariable String code) {
//        TerritoryResponseDTO territory = territoryService.getTerritoryResponseByCode(code);
//        return ResponseEntity.ok(territory);
//    }
//
//    /**
//     * Test territory lookup - get database info
//     */
//    @GetMapping("/lookup/{code}")
//    public ResponseEntity<Map<String, Object>> getTerritoryDbInfo(@PathVariable String code) {
//        Map<String, Object> response = new HashMap<>();
//
//        var dbInfo = territoryDataSourceLookup.getTerritoryDbInfo(code);
//        if (dbInfo.isPresent()) {
//            response.put("found", true);
//            response.put("databaseName", dbInfo.get().getDatabaseName());
//            response.put("databaseUrl", dbInfo.get().getDatabaseUrl());
//            response.put("isActive", dbInfo.get().getIsActive());
//        } else {
//            response.put("found", false);
//        }
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Test territory lookup - check if territory is active
//     */
//    @GetMapping("/lookup/{code}/active")
//    public ResponseEntity<Map<String, Object>> isTerritoryActive(@PathVariable String code) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("code", code);
//        response.put("isActive", territoryDataSourceLookup.isTerritoryActive(code));
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Test territory lookup - get all active codes
//     */
//    @GetMapping("/lookup/codes")
//    public ResponseEntity<List<String>> getAllActiveTerritoryCodes() {
//        List<String> codes = territoryDataSourceLookup.getAllActiveTerritoryCodes();
//        return ResponseEntity.ok(codes);
//    }
//
//    /**
//     * Health check for territory management
//     */
//    @GetMapping("/health")
//    public ResponseEntity<Map<String, Object>> healthCheck() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "OK");
//        response.put("message", "Territory management system is working");
//        response.put("timestamp", System.currentTimeMillis());
//        return ResponseEntity.ok(response);
//    }
//}