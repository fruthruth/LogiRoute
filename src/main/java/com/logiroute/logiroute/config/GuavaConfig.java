package com.logiroute.logiroute.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuavaConfig {

    @Bean
    public RateLimiter rateLimiter(@Value("${logiroute.rate-limiter.per-second:10}") double permitsPerSecond) {
        return RateLimiter.create(permitsPerSecond);
    }
}