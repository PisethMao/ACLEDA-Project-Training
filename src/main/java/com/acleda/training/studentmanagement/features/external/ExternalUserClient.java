package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExternalUserClient {
    private final WebClient jsonPlaceholderWebClient;
    private final ExternalPageMapper externalPageMapper;

    public Page<ExternalUserResponse> getUsers(
            Pageable pageable
    ) {
        ResponseEntity<List<ExternalUserResponse>> response =
                jsonPlaceholderWebClient
                        .get()
                        .uri(
                                uriBuilder -> uriBuilder
                                        .path("/users")
                                        .queryParam(
                                                "_start",
                                                pageable.getOffset()
                                        )
                                        .queryParam(
                                                "_limit",
                                                pageable.getPageSize()
                                        )
                                        .build()
                        )
                        .retrieve()
                        .toEntity(
                                new ParameterizedTypeReference<
                                        List<ExternalUserResponse>
                                        >() {
                                }
                        )
                        .block();
        assert response != null;
        return externalPageMapper
                .toPage(
                        response,
                        pageable
                );
    }

    public ExternalUserResponse getUser(
            Long userId
    ) {
        return jsonPlaceholderWebClient
                .get()
                .uri(
                        "/users/{userId}",
                        userId
                )
                .retrieve()
                .bodyToMono(
                        ExternalUserResponse.class
                )
                .block();
    }
}