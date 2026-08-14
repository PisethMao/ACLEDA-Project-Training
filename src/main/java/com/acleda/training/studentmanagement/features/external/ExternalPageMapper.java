package com.acleda.training.studentmanagement.features.external;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalPageMapper {
    public <T> Page<T> toPage(
            ResponseEntity<List<T>> response,
            Pageable pageable
    ) {
        List<T> content =
                response.getBody() != null
                        ? response.getBody()
                        : List.of();
        String totalCount =
                response.getHeaders()
                        .getFirst("X-Total-Count");
        long totalElements =
                totalCount != null
                        ? Long.parseLong(totalCount)
                        : content.size();
        return new PageImpl<>(
                content,
                pageable,
                totalElements
        );
    }
}