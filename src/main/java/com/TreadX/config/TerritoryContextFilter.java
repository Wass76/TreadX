package com.TreadX.config;

// import com.TreadX.user.service.SecurityContextService; // Will be used later
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter to set the territory context for each request.
 * This filter determines which territory the request should be routed to
 * based on user permissions, request parameters, or headers.
 */
@Component
@RequiredArgsConstructor
@Order(1) // High priority to set context early
public class TerritoryContextFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(TerritoryContextFilter.class);
    
    // private final SecurityContextService securityContextService; // Will be used later
    
    // Paths that don't need territory context
    private static final List<String> EXCLUDED_PATHS = List.of(
        "/actuator/",
        "/v3/api-docs",
        "/swagger-ui/",
        "/api/v1/auth/",
        "/api/v1/territories/", // Territory management endpoints
        "/api/v1/users/", // User management endpoints
        "/api/v1/test/" // Test endpoints
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        try {
            // Skip territory context for excluded paths
            if (shouldSkipTerritoryContext(requestPath)) {
                log.debug("Skipping territory context for path: {}", requestPath);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Determine territory code for this request
            String territoryCode = determineTerritoryCode(request);
            
            if (territoryCode != null) {
                log.debug("Setting territory context for path {}: {}", requestPath, territoryCode);
                TerritoryContextHolder.setTerritoryCode(territoryCode);
            } else {
                log.debug("No territory code determined for path: {}", requestPath);
            }
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
        } finally {
            // Always clear the territory context after the request
            TerritoryContextHolder.clear();
        }
    }
    
    /**
     * Check if the request path should skip territory context
     */
    private boolean shouldSkipTerritoryContext(String requestPath) {
        return EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith);
    }
    
    /**
     * Determine the territory code for this request
     */
    private String determineTerritoryCode(HttpServletRequest request) {
        // Priority 1: Check for explicit territory parameter
        String territoryParam = request.getParameter("territory");
        if (territoryParam != null && !territoryParam.trim().isEmpty()) {
            log.debug("Using territory from request parameter: {}", territoryParam);
            return territoryParam.trim().toUpperCase();
        }
        
        // Priority 2: Check for territory header
        String territoryHeader = request.getHeader("X-Territory-Code");
        if (territoryHeader != null && !territoryHeader.trim().isEmpty()) {
            log.debug("Using territory from header: {}", territoryHeader);
            return territoryHeader.trim().toUpperCase();
        }
        
        // Priority 3: Extract from URL path (e.g., /api/v1/leads/territories/N6B)
        String territoryFromPath = extractTerritoryFromPath(request.getRequestURI());
        if (territoryFromPath != null) {
            log.debug("Using territory from URL path: {}", territoryFromPath);
            return territoryFromPath;
        }
        
        // Priority 4: Use user's primary territory (automatic routing)
        try {
            String userPrimaryTerritory = getUserPrimaryTerritory();
            if (userPrimaryTerritory != null) {
                log.debug("Using user's primary territory: {}", userPrimaryTerritory);
                return userPrimaryTerritory;
            }
        } catch (Exception e) {
            log.debug("Could not determine user's primary territory: {}", e.getMessage());
        }
        
        log.debug("No territory code determined for request");
        return null;
    }
    
    /**
     * Extract territory code from URL path
     */
    private String extractTerritoryFromPath(String requestUri) {
        // Pattern: /api/v1/leads/territories/{territoryCode}
        if (requestUri.contains("/territories/")) {
            String[] parts = requestUri.split("/territories/");
            if (parts.length > 1) {
                String territoryPart = parts[1];
                // Remove any additional path segments
                String territoryCode = territoryPart.split("/")[0];
                if (territoryCode.matches("[A-Z0-9]+")) {
                    return territoryCode;
                }
            }
        }
        return null;
    }
    
    /**
     * Get the current user's primary territory
     */
    private String getUserPrimaryTerritory() {
        try {
            // This will be implemented when SecurityContextService is available
            // For now, return null to avoid circular dependency
            return null;
        } catch (Exception e) {
            log.debug("Error getting user's primary territory: {}", e.getMessage());
            return null;
        }
    }
} 