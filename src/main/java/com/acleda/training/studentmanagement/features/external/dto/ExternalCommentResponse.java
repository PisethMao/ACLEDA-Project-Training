package com.acleda.training.studentmanagement.features.external.dto;

public record ExternalCommentResponse(
        Long postId,
        Long id,
        String name,
        String email,
        String body
) {
}