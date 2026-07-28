package com.qms.qms.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "garmentTypes", "garmentLocations", "defectItems", "purchaseOrders",
                "staff", "factories", "lines", "groups", "customers", "defects");
        // maximumSize bounds "purchaseOrders", whose cache key includes the free-text `search`
        // param and would otherwise grow unbounded as users type different search strings.
        manager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(24)).maximumSize(2000));
        return manager;
    }
}
