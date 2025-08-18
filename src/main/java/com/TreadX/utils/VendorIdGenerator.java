package com.TreadX.utils;

import org.springframework.stereotype.Component;

@Component
public class VendorIdGenerator {
    
    private static final String PREFIX = "001010001";
    
    /**
     * Generates a vendor unique ID in the format: 001010001 + vendorId
     * For example: vendorId = 1 -> vendorUniqueId = 0010100011
     * 
     * @param vendorId The vendor's database ID
     * @return The formatted vendor unique ID
     */
    public static String generateVendorUniqueId(Long vendorId) {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        return PREFIX + vendorId;
    }
    
    /**
     * Extracts the vendor ID from a vendor unique ID
     * 
     * @param vendorUniqueId The vendor unique ID in format 001010001 + vendorId
     * @return The vendor ID, or null if the format is invalid
     */
    public static Long extractVendorId(String vendorUniqueId) {
        if (vendorUniqueId == null || vendorUniqueId.length() <= PREFIX.length()) {
            return null;
        }
        
        try {
            String vendorIdStr = vendorUniqueId.substring(PREFIX.length());
            return Long.parseLong(vendorIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Validates if a vendor unique ID follows the correct format
     * 
     * @param vendorUniqueId The vendor unique ID to validate
     * @return true if the format is valid, false otherwise
     */
    public static boolean isValidVendorUniqueId(String vendorUniqueId) {
        if (vendorUniqueId == null || vendorUniqueId.length() <= PREFIX.length()) {
            return false;
        }
        
        if (!vendorUniqueId.startsWith(PREFIX)) {
            return false;
        }
        
        try {
            String vendorIdStr = vendorUniqueId.substring(PREFIX.length());
            Long.parseLong(vendorIdStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 