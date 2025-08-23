package com.TreadX.utils;

import org.springframework.stereotype.Component;

@Component
public class VendorIdGenerator {
    
    private static final String PREFIX = "001010001"; // Country 001, Province 01, City 0001
    private static final int VENDOR_ID_DIGITS = 6;
    
    /**
     * Generates a vendor unique ID in the format: Country(3) + Province(2) + City(4) + VendorId(6)
     * Total: 15 digits
     * For example: vendorId = 1 -> vendorUniqueId = 001010001000001
     *              vendorId = 123 -> vendorUniqueId = 001010001000123
     *              vendorId = 123456 -> vendorUniqueId = 001010001123456
     * 
     * @param vendorId The vendor's database ID
     * @return The formatted vendor unique ID (15 digits)
     */
    public static String generateVendorUniqueId(Long vendorId) {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        
        // Use NumeralConverter to ensure standard numerals regardless of locale
        String paddedVendorId = NumeralConverter.formatWithLeadingZeros(vendorId, VENDOR_ID_DIGITS);
        return PREFIX + paddedVendorId;
    }
    
    /**
     * Extracts the vendor ID from a vendor unique ID
     * 
     * @param vendorUniqueId The vendor unique ID in format Country(3) + Province(2) + City(4) + VendorId(6)
     * @return The vendor ID, or null if the format is invalid
     */
    public static Long extractVendorId(String vendorUniqueId) {
        if (vendorUniqueId == null || vendorUniqueId.length() != 15) {
            return null;
        }
        
        try {
            // Extract the last 6 digits as vendor ID
            String vendorIdStr = vendorUniqueId.substring(vendorUniqueId.length() - VENDOR_ID_DIGITS);
            return Long.parseLong(vendorIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Extracts geographic information from a vendor unique ID
     * 
     * @param vendorUniqueId The vendor unique ID
     * @return Array with [countryCode, provinceCode, cityCode] or null if invalid
     */
    public static String[] extractGeographicInfo(String vendorUniqueId) {
        if (vendorUniqueId == null || vendorUniqueId.length() != 15) {
            return null;
        }
        
        try {
            String countryCode = vendorUniqueId.substring(0, 3);
            String provinceCode = vendorUniqueId.substring(3, 5);
            String cityCode = vendorUniqueId.substring(5, 9); // City is now 4 digits
            return new String[]{countryCode, provinceCode, cityCode};
        } catch (Exception e) {
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
        if (vendorUniqueId == null || vendorUniqueId.length() != 15) {
            return false;
        }
        
        if (!vendorUniqueId.startsWith(PREFIX)) {
            return false;
        }
        
        try {
            String vendorIdStr = vendorUniqueId.substring(9); // Start after city code (9 digits)
            Long.parseLong(vendorIdStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 