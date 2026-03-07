package com.TreadX.utils;

import org.springframework.stereotype.Component;

@Component
public class DealerIdGenerator {
    
    private static final String PREFIX = "001010001"; // Country 001, Province 01, City 0001
    private static final int DEALER_ID_DIGITS = 6;
    
    /**
     * Generates a vendor unique ID in the format: Country(3) + Province(2) + City(4) + VendorId(6)
     * Total: 15 digits
     * For example: vendorId = 1 -> vendorUniqueId = 001010001000001
     *              vendorId = 123 -> vendorUniqueId = 001010001000123
     *              vendorId = 123456 -> vendorUniqueId = 001010001123456
     * 
     * @param dealerId The dealer's database ID
     * @return The formatted dealer unique ID (15 digits)
     */
    public static String generateDealerUniqueId(Long dealerId) {
        if (dealerId == null) {
            throw new IllegalArgumentException("Dealer ID cannot be null");
        }
        
        // Use NumeralConverter to ensure standard numerals regardless of locale
        String paddedDealerId = NumeralConverter.formatWithLeadingZeros(dealerId, DEALER_ID_DIGITS);
        return PREFIX + paddedDealerId;
    }
    
    /**
     * Extracts the vendor ID from a vendor unique ID
     * 
     * @param dealerUniqueId The dealer unique ID in format Country(3) + Province(2) + City(4) + DealerId(6)
     * @return The dealer ID, or null if the format is invalid
     */
    public static Long extractDealerId(String dealerUniqueId) {
        if (dealerUniqueId == null || dealerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            // Extract the last 6 digits as dealer ID
            String dealerIdStr = dealerUniqueId.substring(dealerUniqueId.length() - DEALER_ID_DIGITS);
            return Long.parseLong(dealerIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Extracts geographic information from a vendor unique ID
     * 
     * @param dealerUniqueId The dealer unique ID
     * @return Array with [countryCode, provinceCode, cityCode] or null if invalid
     */
    public static String[] extractGeographicInfo(String dealerUniqueId) {
        if (dealerUniqueId == null || dealerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            String countryCode = dealerUniqueId.substring(0, 3);
            String provinceCode = dealerUniqueId.substring(3, 5);
            String cityCode = dealerUniqueId.substring(5, 9); // City is now 4 digits
            return new String[]{countryCode, provinceCode, cityCode};
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validates if a dealer unique ID follows the correct format
     * 
     * @param dealerUniqueId The dealer unique ID to validate
     * @return true if the format is valid, false otherwise
     */
    public static boolean isValidDealerUniqueId(String dealerUniqueId) {
        if (dealerUniqueId == null || dealerUniqueId.length() != 15) {
            return false;
        }
        
        if (!dealerUniqueId.startsWith(PREFIX)) {
            return false;
        }
        
        try {
            String dealerIdStr = dealerUniqueId.substring(9); // Start after city code (9 digits)
            Long.parseLong(dealerIdStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 