package com.logiroute.logiroute.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GuavaConfig {

    @Bean
    public CacheBuilder<Object, Object> cacheBuilder() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats();
    }

    @Bean
    public RateLimiter rateLimiter() {
        // Limitar a 10 peticiones por segundo
        return RateLimiter.create(10.0);
    }
}