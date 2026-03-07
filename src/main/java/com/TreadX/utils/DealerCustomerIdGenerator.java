package com.TreadX.utils;

import org.springframework.stereotype.Component;

@Component
public class DealerCustomerIdGenerator {
    
    private static final int CUSTOMER_ID_DIGITS = 6;
    
    /**
     * Generates a dealerDealerCustomer unique ID in the format: Country(3) + Province(2) + City(4) + DealerCustomerId(6)
     * Total: 15 digits
     * For example: 
     * - vendorUniqueId = 001010001000001, dealerDealerCustomerId = 1 -> dealerDealerCustomerUniqueId = 001010001000001
     * - vendorUniqueId = 001010001000001, dealerDealerCustomerId = 123 -> dealerDealerCustomerUniqueId = 001010001000123
     * - vendorUniqueId = 001010001000001, dealerDealerCustomerId = 123456 -> dealerDealerCustomerUniqueId = 001010001123456
     * 
     * @param vendorUniqueId The vendor's unique ID (to extract geographic info)
     * @param dealerDealerCustomerId The dealerDealerCustomer's database ID
     * @return The formatted dealerDealerCustomer unique ID (15 digits)
     */
    public static String generateDealerCustomerUniqueId(String dealerUniqueId, Long dealerDealerCustomerId) {
        if (dealerUniqueId == null || dealerDealerCustomerId == null) {
            throw new IllegalArgumentException("Dealer unique ID and dealerDealerCustomer ID cannot be null");
        }
        
        // Extract geographic info from dealer unique ID
        String[] geographicInfo = DealerIdGenerator.extractGeographicInfo(dealerUniqueId);
        if (geographicInfo == null) {
            throw new IllegalArgumentException("Invalid dealer unique ID format");
        }
        
        String countryCode = geographicInfo[0];
        String provinceCode = geographicInfo[1];
        String cityCode = geographicInfo[2];
        
        // Build dealerDealerCustomer unique ID: Country(3) + Province(2) + City(4) + DealerCustomerId(6)
        String geographicPrefix = countryCode + provinceCode + cityCode;
        String paddedDealerCustomerId = NumeralConverter.formatWithLeadingZeros(dealerDealerCustomerId, CUSTOMER_ID_DIGITS);
        return geographicPrefix + paddedDealerCustomerId;
    }
    
    /**
     * Generates a dealerDealerCustomer unique ID directly from geographic components
     * 
     * @param countryCode 3-digit country code
     * @param provinceCode 2-digit province code
     * @param cityCode 4-digit city code
     * @param dealerDealerCustomerId The dealerDealerCustomer's database ID
     * @return The formatted dealerDealerCustomer unique ID (15 digits)
     */
    public static String generateDealerCustomerUniqueId(String countryCode, String provinceCode, String cityCode, Long dealerDealerCustomerId) {
        if (countryCode == null || provinceCode == null || cityCode == null || dealerDealerCustomerId == null) {
            throw new IllegalArgumentException("All parameters cannot be null");
        }
        
        if (countryCode.length() != 3 || provinceCode.length() != 2 || cityCode.length() != 4) {
            throw new IllegalArgumentException("Invalid geographic code lengths");
        }
        
        // Build dealerDealerCustomer unique ID: Country(3) + Province(2) + City(4) + DealerCustomerId(6)
        String geographicPrefix = countryCode + provinceCode + cityCode;
        String paddedDealerCustomerId = NumeralConverter.formatWithLeadingZeros(dealerDealerCustomerId, CUSTOMER_ID_DIGITS);
        return geographicPrefix + paddedDealerCustomerId;
    }
    
    /**
     * Extracts the dealerDealerCustomer ID from a dealerDealerCustomer unique ID
     * 
     * @param dealerDealerCustomerUniqueId The dealerDealerCustomer unique ID
     * @return The dealerDealerCustomer ID, or null if the format is invalid
     */
    public static Long extractDealerCustomerId(String dealerDealerCustomerUniqueId) {
        if (dealerDealerCustomerUniqueId == null || dealerDealerCustomerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            // Extract the last 6 digits as dealerDealerCustomer ID
            String dealerDealerCustomerIdStr = dealerDealerCustomerUniqueId.substring(dealerDealerCustomerUniqueId.length() - CUSTOMER_ID_DIGITS);
            return Long.parseLong(dealerDealerCustomerIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Extracts geographic information from a dealerDealerCustomer unique ID
     * 
     * @param dealerDealerCustomerUniqueId The dealerDealerCustomer unique ID
     * @return Array with [countryCode, provinceCode, cityCode] or null if invalid
     */
    public static String[] extractGeographicInfo(String dealerDealerCustomerUniqueId) {
        if (dealerDealerCustomerUniqueId == null || dealerDealerCustomerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            String countryCode = dealerDealerCustomerUniqueId.substring(0, 3);
            String provinceCode = dealerDealerCustomerUniqueId.substring(3, 5);
            String cityCode = dealerDealerCustomerUniqueId.substring(5, 9); // City is now 4 digits
            return new String[]{countryCode, provinceCode, cityCode};
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extracts the vendor unique ID from a dealerDealerCustomer unique ID
     * 
     * @param dealerDealerCustomerUniqueId The dealerDealerCustomer unique ID
     * @return The vendor unique ID, or null if the format is invalid
     */
    public static String extractDealerUniqueId(String dealerDealerCustomerUniqueId) {
        if (dealerDealerCustomerUniqueId == null || dealerDealerCustomerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            // Extract geographic info and create vendor unique ID with 6 zeros for vendor ID
            String[] geographicInfo = extractGeographicInfo(dealerDealerCustomerUniqueId);
            if (geographicInfo == null) {
                return null;
            }
            
            // Create vendor unique ID: Country(3) + Province(2) + City(4) + 000000
            String countryCode = geographicInfo[0];
            String provinceCode = geographicInfo[1];
            String cityCode = geographicInfo[2];
            
            return countryCode + provinceCode + cityCode + "000000";
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validates if a dealerDealerCustomer unique ID follows the correct format
     * 
     * @param dealerDealerCustomerUniqueId The dealerDealerCustomer unique ID to validate
     * @return true if the format is valid, false otherwise
     */
    public static boolean isValidDealerCustomerUniqueId(String dealerDealerCustomerUniqueId) {
        if (dealerDealerCustomerUniqueId == null || dealerDealerCustomerUniqueId.length() != 15) {
            return false;
        }
        
        try {
            // Check if the last 6 digits form a valid number
            String dealerDealerCustomerIdStr = dealerDealerCustomerUniqueId.substring(dealerDealerCustomerUniqueId.length() - CUSTOMER_ID_DIGITS);
            Long.parseLong(dealerDealerCustomerIdStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
