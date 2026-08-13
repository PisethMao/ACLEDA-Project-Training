package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.exception.ThirdPartyApiException;
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
                response.getBody() == null
                        ? List.of()
                        : response.getBody();
        String totalCountHeader =
                response
                        .getHeaders()
                        .getFirst("X-Total-Count");
        if (totalCountHeader == null) {
            throw new ThirdPartyApiException(
                    "Third-party API did not return X-Total-Count",
                    null,
                    null
            );
        }
        long totalElements =
                Long.parseLong(
                        totalCountHeader
                );
        return new PageImpl<>(
                content,
                pageable,
                totalElements
        );
    }
}