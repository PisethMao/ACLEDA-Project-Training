package com.acleda.training.studentmanagement.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedisDebugConfig {
    private final CacheManager cacheManager;

    @PostConstruct
    public void checkCacheManager() {
        System.out.println(
                "CACHE MANAGER = "
                        + cacheManager.getClass().getName()
        );
        System.out.println(
                "CACHE NAMES = "
                        + cacheManager.getCacheNames()
        );
    }
}