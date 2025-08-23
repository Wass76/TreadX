package com.TreadX.utils;

import org.springframework.stereotype.Component;

@Component
public class CustomerIdGenerator {
    
    private static final int CUSTOMER_ID_DIGITS = 6;
    
    /**
     * Generates a customer unique ID in the format: Country(3) + Province(2) + City(4) + CustomerId(6)
     * Total: 15 digits
     * For example: 
     * - vendorUniqueId = 001010001000001, customerId = 1 -> customerUniqueId = 001010001000001
     * - vendorUniqueId = 001010001000001, customerId = 123 -> customerUniqueId = 001010001000123
     * - vendorUniqueId = 001010001000001, customerId = 123456 -> customerUniqueId = 001010001123456
     * 
     * @param vendorUniqueId The vendor's unique ID (to extract geographic info)
     * @param customerId The customer's database ID
     * @return The formatted customer unique ID (15 digits)
     */
    public static String generateCustomerUniqueId(String vendorUniqueId, Long customerId) {
        if (vendorUniqueId == null || customerId == null) {
            throw new IllegalArgumentException("Vendor unique ID and customer ID cannot be null");
        }
        
        // Extract geographic info from vendor unique ID
        String[] geographicInfo = VendorIdGenerator.extractGeographicInfo(vendorUniqueId);
        if (geographicInfo == null) {
            throw new IllegalArgumentException("Invalid vendor unique ID format");
        }
        
        String countryCode = geographicInfo[0];
        String provinceCode = geographicInfo[1];
        String cityCode = geographicInfo[2];
        
        // Build customer unique ID: Country(3) + Province(2) + City(4) + CustomerId(6)
        String geographicPrefix = countryCode + provinceCode + cityCode;
        String paddedCustomerId = NumeralConverter.formatWithLeadingZeros(customerId, CUSTOMER_ID_DIGITS);
        return geographicPrefix + paddedCustomerId;
    }
    
    /**
     * Generates a customer unique ID directly from geographic components
     * 
     * @param countryCode 3-digit country code
     * @param provinceCode 2-digit province code
     * @param cityCode 4-digit city code
     * @param customerId The customer's database ID
     * @return The formatted customer unique ID (15 digits)
     */
    public static String generateCustomerUniqueId(String countryCode, String provinceCode, String cityCode, Long customerId) {
        if (countryCode == null || provinceCode == null || cityCode == null || customerId == null) {
            throw new IllegalArgumentException("All parameters cannot be null");
        }
        
        if (countryCode.length() != 3 || provinceCode.length() != 2 || cityCode.length() != 4) {
            throw new IllegalArgumentException("Invalid geographic code lengths");
        }
        
        // Build customer unique ID: Country(3) + Province(2) + City(4) + CustomerId(6)
        String geographicPrefix = countryCode + provinceCode + cityCode;
        String paddedCustomerId = NumeralConverter.formatWithLeadingZeros(customerId, CUSTOMER_ID_DIGITS);
        return geographicPrefix + paddedCustomerId;
    }
    
    /**
     * Extracts the customer ID from a customer unique ID
     * 
     * @param customerUniqueId The customer unique ID
     * @return The customer ID, or null if the format is invalid
     */
    public static Long extractCustomerId(String customerUniqueId) {
        if (customerUniqueId == null || customerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            // Extract the last 6 digits as customer ID
            String customerIdStr = customerUniqueId.substring(customerUniqueId.length() - CUSTOMER_ID_DIGITS);
            return Long.parseLong(customerIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Extracts geographic information from a customer unique ID
     * 
     * @param customerUniqueId The customer unique ID
     * @return Array with [countryCode, provinceCode, cityCode] or null if invalid
     */
    public static String[] extractGeographicInfo(String customerUniqueId) {
        if (customerUniqueId == null || customerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            String countryCode = customerUniqueId.substring(0, 3);
            String provinceCode = customerUniqueId.substring(3, 5);
            String cityCode = customerUniqueId.substring(5, 9); // City is now 4 digits
            return new String[]{countryCode, provinceCode, cityCode};
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extracts the vendor unique ID from a customer unique ID
     * 
     * @param customerUniqueId The customer unique ID
     * @return The vendor unique ID, or null if the format is invalid
     */
    public static String extractVendorUniqueId(String customerUniqueId) {
        if (customerUniqueId == null || customerUniqueId.length() != 15) {
            return null;
        }
        
        try {
            // Extract geographic info and create vendor unique ID with 6 zeros for vendor ID
            String[] geographicInfo = extractGeographicInfo(customerUniqueId);
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
     * Validates if a customer unique ID follows the correct format
     * 
     * @param customerUniqueId The customer unique ID to validate
     * @return true if the format is valid, false otherwise
     */
    public static boolean isValidCustomerUniqueId(String customerUniqueId) {
        if (customerUniqueId == null || customerUniqueId.length() != 15) {
            return false;
        }
        
        try {
            // Check if the last 6 digits form a valid number
            String customerIdStr = customerUniqueId.substring(customerUniqueId.length() - CUSTOMER_ID_DIGITS);
            Long.parseLong(customerIdStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
