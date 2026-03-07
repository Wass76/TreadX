package com.TreadX.user.config;

/**
 * Constants for system roles to avoid hardcoding role names
 */
public final class RoleConstants {
    
    // Platform roles
    public static final String PLATFORM_ADMIN = "PLATFORM_ADMIN";
    public static final String SALES_MANAGER = "SALES_MANAGER";
    public static final String SALES_AGENT = "SALES_AGENT";
    public static final String SUPPORT_AGENT = "SUPPORT_AGENT";
    
    // Vendor roles
    public static final String DEALER_ADMIN = "DEALER_ADMIN";
    public static final String DEALER_EMPLOYEE = "DEALER_EMPLOYEE";
    public static final String DEALER_TECHNICIAN = "DEALER_TECHNICIAN";
    
    private RoleConstants() {
        // Utility class, prevent instantiation
    }
} 