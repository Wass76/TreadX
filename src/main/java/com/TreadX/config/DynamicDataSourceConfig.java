package com.TreadX.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Dynamic DataSource configuration that routes database connections
 * based on the current territory code from TerritoryContextHolder.
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(name = "treadx.dynamic-datasource.enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceConfig {
    
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceConfig.class);
    

    @Autowired
    private Environment env;

    /**
     * Primary DataSource that routes to territory-specific databases
     */
    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource) {
        log.info("Initializing dynamic DataSource configuration (from properties)");
        Map<Object, Object> targetDataSources = new HashMap<>();

        // Load all tenant datasources from properties
        // Property prefix: spring.datasource.tenants.{tenantKey}.*
        String tenantsPrefix = "spring.datasource.tenants.";
        for (String key : env.getProperty(tenantsPrefix, Properties.class, new Properties()).stringPropertyNames()) {
            // Each key is like 'district1.url', 'district1.username', etc.
            // We want unique tenant keys (district1, district2, ...)
            String[] parts = key.split("\\.");
            if (parts.length < 2) continue;
            String tenantKey = parts[0];
            if (targetDataSources.containsKey(tenantKey)) continue; // already created
            // Build DataSource for this tenant
            String prefix = tenantsPrefix + tenantKey + ".";
            String url = env.getProperty(prefix + "url");
            String username = env.getProperty(prefix + "username");
            String password = env.getProperty(prefix + "password");
            String driver = env.getProperty(prefix + "driver-class-name", "org.postgresql.Driver");
            int maxPool = env.getProperty(prefix + "hikari.maximum-pool-size", Integer.class, 5);
            int minIdle = env.getProperty(prefix + "hikari.minimum-idle", Integer.class, 1);
            if (url == null || username == null || password == null) continue;
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setDriverClassName(driver);
            ds.setMaximumPoolSize(maxPool);
            ds.setMinimumIdle(minIdle);
            ds.setPoolName("TenantDataSource-" + tenantKey);
            targetDataSources.put(tenantKey, ds);
            log.info("Registered tenant datasource: {} -> {}", tenantKey, url);
        }

        // Routing datasource
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
        log.info("Dynamic DataSource configuration initialized ({} tenants)", targetDataSources.size());
        return routingDataSource;
    }
    
    /**
     * Custom routing DataSource that resolves the target DataSource dynamically
     */
    private static class DynamicRoutingDataSource extends AbstractRoutingDataSource {
        
        @Override
        protected Object determineCurrentLookupKey() {
            String key = TerritoryContextHolder.getTerritoryCode();
            LoggerFactory.getLogger(DynamicRoutingDataSource.class).info("[DynamicRoutingDataSource] Current lookup key (territory code): {}", key);
            return key;
        }
    }
} 