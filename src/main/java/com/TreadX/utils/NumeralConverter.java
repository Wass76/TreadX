package com.TreadX.utils;

import org.springframework.stereotype.Component;

@Component
public class NumeralConverter {
    
    /**
     * Converts Arabic numerals to standard numerals
     * This is a reliable way to ensure consistent number formatting regardless of system locale
     */
    public static String convertToStandardNumerals(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        return input
            .replace('٠', '0')
            .replace('١', '1')
            .replace('٢', '2')
            .replace('٣', '3')
            .replace('٤', '4')
            .replace('٥', '5')
            .replace('٦', '6')
            .replace('٧', '7')
            .replace('٨', '8')
            .replace('٩', '9');
    }
    
    /**
     * Converts a number to string with leading zeros using standard numerals
     * This bypasses any locale-specific formatting
     */
    public static String formatWithLeadingZeros(Long number, int digits) {
        if (number == null) {
            return "0".repeat(digits);
        }
        
        String numStr = number.toString();
        int currentLength = numStr.length();
        
        if (currentLength >= digits) {
            return numStr;
        }
        
        // Add leading zeros manually to avoid locale issues
        int zerosNeeded = digits - currentLength;
        return "0".repeat(zerosNeeded) + numStr;
    }
    
    /**
     * Ensures a string contains only standard numerals
     */
    public static boolean containsOnlyStandardNumerals(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        return input.matches("^[0-9]+$");
    }
    
    /**
     * Converts and validates a numeric string
     */
    public static String convertAndValidate(String input) {
        String converted = convertToStandardNumerals(input);
        if (containsOnlyStandardNumerals(converted)) {
            return converted;
        }
        throw new IllegalArgumentException("Input contains non-numeric characters after conversion: " + input);
    }
}
