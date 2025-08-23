package com.TreadX.config;

import com.TreadX.utils.auditing.ApplicationAuditingAware;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import jakarta.annotation.PostConstruct;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
@EnableCaching
@Order(1)
public class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public AuditorAware<Long> auditorAware(){
        return new ApplicationAuditingAware();
    }

//    @Bean
//    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    public CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager("books", "patrons" );
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DataSource defaultDataSource(DataSourceProperties properties) {
        log.info("Default DataSource properties: url={}, username={}, password={}, driverClassName={}",
            properties.getUrl(), properties.getUsername(), properties.getPassword(), properties.getDriverClassName());
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        return dataSource;
    }

    /**
     * Configure default locale to US to prevent Arabic numeral formatting
     */
    @Bean
    @Primary
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
    
    /**
     * Set JVM default locale to US to ensure consistent number formatting
     * This method runs after the bean is constructed
     */
    @PostConstruct
    public void setDefaultLocale() {
        // Force US locale at multiple levels
        Locale.setDefault(Locale.US);
        
        // Set system properties
        System.setProperty("user.language", "en");
        System.setProperty("user.country", "US");
        System.setProperty("user.variant", "");
        
        // Force JVM locale settings
        System.setProperty("java.locale.providers", "JRE,SPI");
        System.setProperty("sun.locale.formatasdefault", "true");
        
        // Set default timezone to prevent locale-related issues
        System.setProperty("user.timezone", "UTC");
        
        log.info("Forced application locale to US: {}", Locale.getDefault());
        log.info("System properties set: language={}, country={}", 
            System.getProperty("user.language"), System.getProperty("user.country"));
    }
}
