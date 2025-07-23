//package com.TreadX.test;
//
//import com.TreadX.user.entity.Territory;
//import com.TreadX.user.Enum.TerritoryLevel;
//import com.TreadX.user.repository.TerritoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class SimpleTerritoryTest implements CommandLineRunner {
//
//    @Autowired
//    private TerritoryRepository territoryRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("=== Territory Management Test ===");
//
//        try {
//            // Test basic repository methods
//            System.out.println("Testing basic repository methods...");
//
//            // Test finding all active territories
//            List<Territory> allTerritories = territoryRepository.findByIsActiveTrue();
//            System.out.println("Found " + allTerritories.size() + " active territories");
//
//            // Test finding territories by level
//            List<Territory> districts = territoryRepository.findByLevelAndIsActiveTrue(TerritoryLevel.DISTRICT);
//            System.out.println("Found " + districts.size() + " districts");
//
//            List<Territory> cities = territoryRepository.findByLevelAndIsActiveTrue(TerritoryLevel.CITY);
//            System.out.println("Found " + cities.size() + " cities");
//
//            List<Territory> provinces = territoryRepository.findByLevelAndIsActiveTrue(TerritoryLevel.PROVINCE);
//            System.out.println("Found " + provinces.size() + " provinces");
//
//            List<Territory> countries = territoryRepository.findByLevelAndIsActiveTrue(TerritoryLevel.COUNTRY);
//            System.out.println("Found " + countries.size() + " countries");
//
//            // Test finding territory by code
//            territoryRepository.findByCode("CANADA").ifPresent(territory -> {
//                System.out.println("Found territory: " + territory.getCode() + " - " + territory.getName());
//            });
//
//            // Test finding direct children
//            List<String> childCodes = territoryRepository.findDirectChildTerritoryCodes("CANADA");
//            System.out.println("Canada has " + childCodes.size() + " direct children: " + childCodes);
//
//            // Test finding all active territory codes
//            List<String> allCodes = territoryRepository.findAllActiveTerritoryCodes();
//            System.out.println("All active territory codes: " + allCodes);
//
//            System.out.println("=== Territory Management Test PASSED ===");
//
//        } catch (Exception e) {
//            System.err.println("=== Territory Management Test FAILED ===");
//            System.err.println("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}