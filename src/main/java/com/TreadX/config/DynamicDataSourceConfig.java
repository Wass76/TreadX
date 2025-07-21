package com.TreadX.config;

import com.TreadX.config.TerritoryDataSourceLookup.TerritoryDbInfo;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Dynamic DataSource configuration that routes database connections
 * based on the current territory code from TerritoryContextHolder.
 */
@Configuration
@ConditionalOnProperty(name = "treadx.dynamic-datasource.enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceConfig {
    
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceConfig.class);
    

    private final TerritoryDataSourceLookup territoryDataSourceLookup;

    public DynamicDataSourceConfig(TerritoryDataSourceLookup territoryDataSourceLookup) {
        this.territoryDataSourceLookup = territoryDataSourceLookup;
    }

    /**
     * Primary DataSource that routes to territory-specific databases
     */
    @Bean
    public DataSource dynamicDataSource() {
        log.info("Initializing dynamic DataSource configuration");
        
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        
        // Set default target data source (fallback)
        routingDataSource.setDefaultTargetDataSource(createDefaultDataSource());
        
        // Set resolved data sources (will be populated on first access)
        routingDataSource.setTargetDataSources(new HashMap<>());
        
        // Set data source resolver
        routingDataSource.setDataSourceResolver(this::resolveDataSource);
        
        routingDataSource.afterPropertiesSet();
        
        log.info("Dynamic DataSource configuration initialized");
        return routingDataSource;
    }
    
    /**
     * Secondary DataSource for territory-specific databases (not @Primary)
     */
    @Bean(name = "territoryRoutingDataSource")
    public DataSource territoryRoutingDataSource(@Qualifier("dataSource") DataSource defaultDataSource) {
        log.info("Initializing territory routing DataSource configuration");
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        // Set default target data source (fallback)
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        // Set resolved data sources (will be populated on first access)
        routingDataSource.setTargetDataSources(new HashMap<>());
        // Set data source resolver
        routingDataSource.setDataSourceResolver(this::resolveDataSource);
        routingDataSource.afterPropertiesSet();
        log.info("Territory routing DataSource configuration initialized");
        return routingDataSource;
    }
    
    /**
     * Create default DataSource for fallback
     */
    private DataSource createDefaultDataSource() {
        log.info("Creating default DataSource for fallback");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/treadx");
        dataSource.setUsername("postgres");
        dataSource.setPassword("password");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setPoolName("DefaultDataSource");
        
        return dataSource;
    }
    
    /**
     * Resolve DataSource for the current territory
     */
    private DataSource resolveDataSource() {
        String territoryCode = TerritoryContextHolder.getTerritoryCode();
        
        if (territoryCode == null) {
            log.warn("No territory code set, using default DataSource");
            return createDefaultDataSource();
        }
        
        try {
            log.debug("Resolving DataSource for territory: {}", territoryCode);
            Optional<TerritoryDbInfo> dbInfo = territoryDataSourceLookup.getTerritoryDbInfo(territoryCode);
            
            if (dbInfo.isPresent()) {
                return createTerritoryDataSource(dbInfo.get());
            } else {
                log.warn("Territory not found or inactive: {}", territoryCode);
                return createDefaultDataSource();
            }
            
        } catch (Exception e) {
            log.error("Failed to resolve DataSource for territory: {}", territoryCode, e);
            log.warn("Falling back to default DataSource");
            return createDefaultDataSource();
        }
    }
    
    /**
     * Create DataSource for a specific territory
     */
    private DataSource createTerritoryDataSource(TerritoryDbInfo dbInfo) {
        log.debug("Creating DataSource for territory: {} -> {}", dbInfo.getDatabaseName(), dbInfo.getDatabaseUrl());
        
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbInfo.getDatabaseUrl());
        dataSource.setUsername(dbInfo.getDatabaseUsername());
        dataSource.setPassword(dbInfo.getDatabasePassword());
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setPoolName("TerritoryDataSource-" + dbInfo.getDatabaseName());
        
        // Set connection pool properties
        dataSource.setConnectionTimeout(30000); // 30 seconds
        dataSource.setIdleTimeout(600000); // 10 minutes
        dataSource.setMaxLifetime(1800000); // 30 minutes
        
        return dataSource;
    }
    
    /**
     * Custom routing DataSource that resolves the target DataSource dynamically
     */
    private static class DynamicRoutingDataSource extends AbstractRoutingDataSource {
        
        private DataSourceResolver dataSourceResolver;
        
        public void setDataSourceResolver(DataSourceResolver dataSourceResolver) {
            this.dataSourceResolver = dataSourceResolver;
        }
        
        @Override
        protected Object determineCurrentLookupKey() {
            // Return the territory code as the lookup key
            return TerritoryContextHolder.getTerritoryCode();
        }
        
        @Override
        protected DataSource determineTargetDataSource() {
            if (dataSourceResolver != null) {
                return dataSourceResolver.resolveDataSource();
            }
            return super.determineTargetDataSource();
        }
    }
    
    /**
     * Functional interface for DataSource resolution
     */
    @FunctionalInterface
    private interface DataSourceResolver {
        DataSource resolveDataSource();
    }
} 