package com.acleda.training.studentmanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.cache")
public record CacheProperties(
        Duration studentTtl,
        Duration departmentTtl,
        Duration courseTtl,
        Duration instructorTtl,
        Duration courseOfferingTtl,
        Duration externalUserTtl,
        Duration externalUsersPageTtl
) {
}