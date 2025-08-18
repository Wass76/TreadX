package com.TreadX.test;

import com.TreadX.utils.VendorIdGenerator;

public class VendorIdGeneratorTest {
    
    public static void main(String[] args) {
        // Test vendor ID generation
        System.out.println("Testing Vendor ID Generation:");
        System.out.println("Vendor ID 1 -> " + VendorIdGenerator.generateVendorUniqueId(1L));
        System.out.println("Vendor ID 10 -> " + VendorIdGenerator.generateVendorUniqueId(10L));
        System.out.println("Vendor ID 100 -> " + VendorIdGenerator.generateVendorUniqueId(100L));
        System.out.println("Vendor ID 1000 -> " + VendorIdGenerator.generateVendorUniqueId(1000L));
        
        // Test vendor ID extraction
        System.out.println("\nTesting Vendor ID Extraction:");
        System.out.println("From 0010100011 -> " + VendorIdGenerator.extractVendorId("0010100011"));
        System.out.println("From 00101000110 -> " + VendorIdGenerator.extractVendorId("00101000110"));
        System.out.println("From 001010001100 -> " + VendorIdGenerator.extractVendorId("001010001100"));
        
        // Test validation
        System.out.println("\nTesting Validation:");
        System.out.println("0010100011 is valid: " + VendorIdGenerator.isValidVendorUniqueId("0010100011"));
        System.out.println("00101000110 is valid: " + VendorIdGenerator.isValidVendorUniqueId("00101000110"));
        System.out.println("VND-123 is valid: " + VendorIdGenerator.isValidVendorUniqueId("VND-123"));
        System.out.println("null is valid: " + VendorIdGenerator.isValidVendorUniqueId(null));
    }
} 