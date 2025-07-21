package com.TreadX.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ThreadLocal holder for the current territory code.
 * This allows the application to know which territory's database to connect to
 * for the current request thread.
 */
public class TerritoryContextHolder {
    
    private static final Logger log = LoggerFactory.getLogger(TerritoryContextHolder.class);
    
    private static final ThreadLocal<String> territoryContext = new ThreadLocal<>();
    
    /**
     * Set the territory code for the current thread
     */
    public static void setTerritoryCode(String territoryCode) {
        log.debug("Setting territory code for thread {}: {}", Thread.currentThread().threadId(), territoryCode);
        territoryContext.set(territoryCode);
    }
    
    /**
     * Get the territory code for the current thread
     */
    public static String getTerritoryCode() {
        String code = territoryContext.get();
        log.debug("Getting territory code for thread {}: {}", Thread.currentThread().threadId(), code);
        return code;
    }
    
    /**
     * Check if a territory code is set for the current thread
     */
    public static boolean hasTerritoryCode() {
        return territoryContext.get() != null;
    }
    
    /**
     * Clear the territory code for the current thread
     */
    public static void clear() {
        log.debug("Clearing territory code for thread {}", Thread.currentThread().threadId());
        territoryContext.remove();
    }
    
    /**
     * Get the territory code or throw an exception if not set
     */
    public static String getRequiredTerritoryCode() {
        String code = getTerritoryCode();
        if (code == null) {
            throw new IllegalStateException("No territory code set for current thread");
        }
        return code;
    }
} 