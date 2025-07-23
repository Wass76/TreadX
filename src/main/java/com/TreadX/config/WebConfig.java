package com.TreadX.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<TerritoryContextFilter> territoryContextFilterRegistration(TerritoryContextFilter filter) {
        FilterRegistrationBean<TerritoryContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*"); // Apply to all URLs
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // Run before all other filters
        return registration;
    }
} 