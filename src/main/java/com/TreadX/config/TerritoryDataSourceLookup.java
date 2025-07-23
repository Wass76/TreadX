package com.TreadX.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC-based lookup for territory database information.
 * This avoids circular dependency by not using JPA repositories or services.
 */
@Component
public class TerritoryDataSourceLookup {
    
    private static final Logger log = LoggerFactory.getLogger(TerritoryDataSourceLookup.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public TerritoryDataSourceLookup(@Qualifier("defaultDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
     * Get territory database information by code
     */
    public Optional<TerritoryDbInfo> getTerritoryDbInfo(String code) {
        try {
            log.debug("Looking up database info for territory: {}", code);
            
            Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT database_url, database_name, database_username, database_password, is_active " +
                "FROM territories WHERE code = ? AND is_active = true",
                code
            );
            
            TerritoryDbInfo dbInfo = new TerritoryDbInfo(
                (String) result.get("database_url"),
                (String) result.get("database_name"),
                (String) result.get("database_username"),
                (String) result.get("database_password"),
                (Boolean) result.get("is_active")
            );
            
            log.debug("Found database info for territory {}: {}", code, dbInfo.getDatabaseName());
            return Optional.of(dbInfo);
            
        } catch (Exception e) {
            log.warn("Could not find database info for territory: {}", code, e);
            return Optional.empty();
        }
    }
    
    /**
     * Check if territory exists and is active
     */
    public boolean isTerritoryActive(String code) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM territories WHERE code = ? AND is_active = true",
                Integer.class,
                code
            );
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("Error checking if territory is active: {}", code, e);
            return false;
        }
    }
    
    /**
     * Get all active territory codes
     */
    public java.util.List<String> getAllActiveTerritoryCodes() {
        try {
            return jdbcTemplate.queryForList(
                "SELECT code FROM territories WHERE is_active = true ORDER BY code",
                String.class
            );
        } catch (Exception e) {
            log.warn("Error getting active territory codes", e);
            return java.util.List.of();
        }
    }
    
    /**
     * Data class for territory database information
     */
    public static class TerritoryDbInfo {
        private final String databaseUrl;
        private final String databaseName;
        private final String databaseUsername;
        private final String databasePassword;
        private final Boolean isActive;
        
        public TerritoryDbInfo(String databaseUrl, String databaseName, 
                              String databaseUsername, String databasePassword, Boolean isActive) {
            this.databaseUrl = databaseUrl;
            this.databaseName = databaseName;
            this.databaseUsername = databaseUsername;
            this.databasePassword = databasePassword;
            this.isActive = isActive;
        }
        
        public String getDatabaseUrl() { return databaseUrl; }
        public String getDatabaseName() { return databaseName; }
        public String getDatabaseUsername() { return databaseUsername; }
        public String getDatabasePassword() { return databasePassword; }
        public Boolean getIsActive() { return isActive; }
        
        @Override
        public String toString() {
            return "TerritoryDbInfo{" +
                    "databaseName='" + databaseName + '\'' +
                    ", databaseUrl='" + databaseUrl + '\'' +
                    ", isActive=" + isActive +
                    '}';
        }
    }
} 