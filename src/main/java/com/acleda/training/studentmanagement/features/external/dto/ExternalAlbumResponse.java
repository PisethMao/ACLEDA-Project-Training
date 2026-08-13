package com.acleda.training.studentmanagement.features.external.dto;

public record ExternalAlbumResponse(
        Long userId,
        Long id,
        String title
) {
}