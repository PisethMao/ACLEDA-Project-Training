package com.acleda.training.studentmanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(
        prefix = "third-party.json-placeholder"
)
public record JsonPlaceholderProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration responseTimeout
) {
}