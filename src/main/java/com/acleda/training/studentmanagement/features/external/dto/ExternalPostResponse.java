package com.acleda.training.studentmanagement.features.external.dto;

public record ExternalPostResponse(
        Long userId,
        Long id,
        String title,
        String body
) {
}