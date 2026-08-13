package com.acleda.training.studentmanagement.features.external;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public record ExternalCachedPage<T>(
        List<T> content,
        int page,
        int size,
        long totalElements
) {
    public static <T> ExternalCachedPage<T> from(
            Page<T> page
    ) {
        return new ExternalCachedPage<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public Page<T> toPage() {
        return new PageImpl<>(
                content,
                PageRequest.of(
                        page,
                        size
                ),
                totalElements
        );
    }
}