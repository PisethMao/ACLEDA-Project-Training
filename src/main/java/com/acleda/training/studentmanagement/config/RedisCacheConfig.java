package com.acleda.training.studentmanagement.config;

import com.acleda.training.studentmanagement.features.course.CourseCacheNames;
import com.acleda.training.studentmanagement.features.course.offering.CourseOfferingCacheNames;
import com.acleda.training.studentmanagement.features.department.DepartmentCacheNames;
import com.acleda.training.studentmanagement.features.external.ExternalCacheNames;
import com.acleda.training.studentmanagement.features.instructor.InstructorCacheNames;
import com.acleda.training.studentmanagement.features.student.StudentCacheNames;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheConfig {
    @Bean
    RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory
    ) {
        var keySerializer =
                RedisSerializationContext.SerializationPair
                        .fromSerializer(
                                RedisSerializer.string()
                        );
        var valueSerializer =
                RedisSerializationContext.SerializationPair
                        .fromSerializer(
                                RedisSerializer.json()
                        );
        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration
                        .defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeKeysWith(keySerializer)
                        .serializeValuesWith(valueSerializer)
                        .computePrefixWith(
                                cacheName ->
                                        "student-management:v1:"
                                                + cacheName
                                                + "::"
                        );
        Map<String, RedisCacheConfiguration> configurations =
                Map.of(
                        StudentCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(10)
                        ),
                        DepartmentCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(30)
                        ),
                        CourseCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(20)
                        ),
                        InstructorCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(15)
                        ),
                        CourseOfferingCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(5)
                        ),
                        ExternalCacheNames.USER_BY_ID,
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(5)
                        ),
                        ExternalCacheNames.USERS_PAGE,
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(2)
                        )
                );
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(
                        configurations
                )
                .transactionAware()
                .build();
    }
}