package com.TreadX.address.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling unique ID generation and normalization.
 * Provides methods for generating sequential IDs and converting between Arabic and English numerals.
 */
public final class UniqueIdUtils {
    private static final Logger log = LoggerFactory.getLogger(UniqueIdUtils.class);
    private static final String DEFAULT_STARTING_ID = "0"; // Start from 0 so next will be 1

    // Private constructor to prevent instantiation
    private UniqueIdUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Generates the next unique ID based on the current maximum ID.
     * If no current ID exists, starts with "1" padded to the required length.
     *
     * @param currentMaxId The current maximum ID, or null if no IDs exist yet
     * @param length The required length of the ID
     * @return The next unique ID
     */
    public static String generateNextUniqueId(String currentMaxId, int length) {
        try {
            // First normalize any Arabic numerals to English
            String normalizedId = currentMaxId != null ? 
                normalizeArabicNumeralsToEnglishNumerals(currentMaxId) : 
                DEFAULT_STARTING_ID;
            
            // Parse the normalized ID and increment
            int nextId = Integer.parseInt(normalizedId) + 1;
            
            // Format with leading zeros and ensure it's in English numerals
            String formattedId = String.format("%0" + length + "d", nextId);
            
            log.info("Generated next ID: {} from current: {}", formattedId, currentMaxId);
            return normalizeArabicNumeralsToEnglishNumerals(formattedId);
        } catch (NumberFormatException e) {
            log.error("Error parsing ID: {}", currentMaxId, e);
            // If there's an error, start with 1
            return String.format("%0" + length + "d", 1);
        }
    }

    /**
     * Normalizes Arabic numerals to English numerals in a string.
     *
     * @param input The input string containing Arabic numerals
     * @return The string with Arabic numerals replaced by English numerals
     */
    private static String normalizeArabicNumeralsToEnglishNumerals(String input) {
        if (input == null) return null;
        
        return input.replace('٠', '0')
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
} 