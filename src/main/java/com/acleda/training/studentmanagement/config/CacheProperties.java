package com.acleda.training.studentmanagement.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.cache")
public record CacheProperties(
        @NotNull
        Duration defaultTtl,
        @NotNull
        Duration studentTtl,
        @NotNull
        Duration departmentTtl,
        @NotNull
        Duration courseTtl,
        @NotNull
        Duration instructorTtl,
        @NotNull
        Duration courseOfferingTtl,
        @NotNull
        Duration externalUserTtl,
        @NotNull
        Duration externalUsersPageTtl
) {
}