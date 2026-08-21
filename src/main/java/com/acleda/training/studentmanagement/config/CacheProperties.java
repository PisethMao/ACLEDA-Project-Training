package com.acleda.training.studentmanagement.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.cache")
public record CacheProperties(
        @Valid
        @NotNull
        CacheSpec student,
        @Valid
        @NotNull
        CacheSpec department,
        @Valid
        @NotNull
        CacheSpec course,
        @Valid
        @NotNull
        CacheSpec instructor,
        @Valid
        @NotNull
        CacheSpec courseOffering,
        @Valid
        @NotNull
        CacheSpec externalUser,
        @Valid
        @NotNull
        CacheSpec externalPost,
        @Valid
        @NotNull
        CacheSpec externalComment,
        @Valid
        @NotNull
        CacheSpec externalAlbum
) {
        public record CacheSpec(
                @NotBlank
                String keyPrefix,
                @NotNull
                Duration ttl
        ) {
        }
}