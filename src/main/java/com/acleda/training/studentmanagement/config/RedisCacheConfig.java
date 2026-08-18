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

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheConfig {
    private static final String CACHE_PREFIX =
            "student-management:v1:";

    @Bean
    RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            CacheProperties properties
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
                        .entryTtl(properties.defaultTtl())
                        .disableCachingNullValues()
                        .serializeKeysWith(keySerializer)
                        .serializeValuesWith(valueSerializer)
                        .computePrefixWith(
                                cacheName ->
                                        CACHE_PREFIX
                                                + cacheName
                                                + "::"
                        );
        Map<String, RedisCacheConfiguration> configurations =
                Map.of(
                        StudentCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                properties.studentTtl()
                        ),
                        DepartmentCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                properties.departmentTtl()
                        ),
                        CourseCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                properties.courseTtl()
                        ),
                        InstructorCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                properties.instructorTtl()
                        ),
                        CourseOfferingCacheNames.BY_ID,
                        defaultConfig.entryTtl(
                                properties.courseOfferingTtl()
                        ),
                        ExternalCacheNames.USER_BY_ID,
                        defaultConfig.entryTtl(
                                properties.externalUserTtl()
                        ),
                        ExternalCacheNames.USERS_PAGE,
                        defaultConfig.entryTtl(
                                properties.externalUsersPageTtl()
                        )
                );
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configurations)
                .transactionAware()
                .build();
    }
}