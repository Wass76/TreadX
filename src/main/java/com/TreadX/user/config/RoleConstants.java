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
    public static final String VENDOR_ADMIN = "VENDOR_ADMIN";
    public static final String VENDOR_EMPLOYEE = "VENDOR_EMPLOYEE";
    public static final String VENDOR_TECHNICIAN = "VENDOR_TECHNICIAN";
    
    private RoleConstants() {
        // Utility class, prevent instantiation
    }
} 