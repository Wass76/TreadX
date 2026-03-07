package com.TreadX.test;

import com.TreadX.utils.DealerIdGenerator;

public class DealerIdGeneratorTest {
    
    public static void main(String[] args) {
        // Test vendor ID generation
        System.out.println("Testing Dealer ID Generation:");
        System.out.println("Dealer ID 1 -> " + DealerIdGenerator.generateDealerUniqueId(1L));
        System.out.println("Dealer ID 10 -> " + DealerIdGenerator.generateDealerUniqueId(10L));
        System.out.println("Dealer ID 100 -> " + DealerIdGenerator.generateDealerUniqueId(100L));
        System.out.println("Dealer ID 1000 -> " + DealerIdGenerator.generateDealerUniqueId(1000L));
        
        // Test vendor ID extraction
        System.out.println("\nTesting Dealer ID Extraction:");
        System.out.println("From 0010100011 -> " + DealerIdGenerator.extractDealerId("0010100011"));
        System.out.println("From 00101000110 -> " + DealerIdGenerator.extractDealerId("00101000110"));
        System.out.println("From 001010001100 -> " + DealerIdGenerator.extractDealerId("001010001100"));
        
        // Test validation
        System.out.println("\nTesting Validation:");
        System.out.println("0010100011 is valid: " + DealerIdGenerator.isValidDealerUniqueId("0010100011"));
        System.out.println("00101000110 is valid: " + DealerIdGenerator.isValidDealerUniqueId("00101000110"));
        System.out.println("DLR-123 is valid: " + DealerIdGenerator.isValidDealerUniqueId("DLR-123"));
        System.out.println("null is valid: " + DealerIdGenerator.isValidDealerUniqueId(null));
    }
} 