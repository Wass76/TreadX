//package com.TreadX.test;
//
//import com.TreadX.config.TerritoryContextHolder;
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
//@RequestMapping("/api/v1/test/territory")
//@RequiredArgsConstructor
//public class TerritoryTestController {
//
//    private final TerritoryService territoryService;
//
//    /**
//     * Test territory context
//     */
//    @GetMapping("/context")
//    public ResponseEntity<Map<String, Object>> getTerritoryContext() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("currentTerritoryCode", TerritoryContextHolder.getTerritoryCode());
//        response.put("hasTerritoryCode", TerritoryContextHolder.hasTerritoryCode());
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Test setting territory context
//     */
//    @PostMapping("/context/{territoryCode}")
//    public ResponseEntity<Map<String, Object>> setTerritoryContext(@PathVariable String territoryCode) {
//        TerritoryContextHolder.setTerritoryCode(territoryCode);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Territory context set");
//        response.put("territoryCode", territoryCode);
//        response.put("currentTerritoryCode", TerritoryContextHolder.getTerritoryCode());
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Test clearing territory context
//     */
//    @DeleteMapping("/context")
//    public ResponseEntity<Map<String, Object>> clearTerritoryContext() {
//        TerritoryContextHolder.clear();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Territory context cleared");
//        response.put("currentTerritoryCode", TerritoryContextHolder.getTerritoryCode());
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Get all territories
//     */
//    @GetMapping("/list")
//    public ResponseEntity<List<TerritoryResponseDTO>> getAllTerritories() {
//        List<TerritoryResponseDTO> territories = territoryService.getAllActiveTerritories();
//        return ResponseEntity.ok(territories);
//    }
//
//    /**
//     * Get territories by level
//     */
//    @GetMapping("/level/{level}")
//    public ResponseEntity<List<TerritoryResponseDTO>> getTerritoriesByLevel(@PathVariable TerritoryLevel level) {
//        List<TerritoryResponseDTO> territories = territoryService.getTerritoriesByLevel(level);
//        return ResponseEntity.ok(territories);
//    }
//
//    /**
//     * Get territory by code
//     */
//    @GetMapping("/{code}")
//    public ResponseEntity<TerritoryResponseDTO> getTerritoryByCode(@PathVariable String code) {
//        TerritoryResponseDTO territory = territoryService.getTerritoryResponseByCode(code);
//        return ResponseEntity.ok(territory);
//    }
//
//    /**
//     * Get territory with hierarchy
//     */
//    @GetMapping("/{code}/hierarchy")
//    public ResponseEntity<TerritoryResponseDTO> getTerritoryWithHierarchy(@PathVariable String code) {
//        TerritoryResponseDTO territory = territoryService.getTerritoryByCodeWithHierarchy(code);
//        return ResponseEntity.ok(territory);
//    }
//
//    /**
//     * Get child territories
//     */
//    @GetMapping("/{code}/children")
//    public ResponseEntity<List<TerritoryResponseDTO>> getChildTerritories(@PathVariable String code) {
//        List<TerritoryResponseDTO> territories = territoryService.getChildTerritories(code);
//        return ResponseEntity.ok(territories);
//    }
//
//    /**
//     * Get all territory codes
//     */
//    @GetMapping("/codes")
//    public ResponseEntity<List<String>> getAllTerritoryCodes() {
//        List<String> codes = territoryService.getAllActiveTerritoryCodes();
//        return ResponseEntity.ok(codes);
//    }
//
//    /**
//     * Get territory codes by level
//     */
//    @GetMapping("/codes/level/{level}")
//    public ResponseEntity<List<String>> getTerritoryCodesByLevel(@PathVariable TerritoryLevel level) {
//        List<String> codes = territoryService.getTerritoryCodesByLevel(level);
//        return ResponseEntity.ok(codes);
//    }
//}