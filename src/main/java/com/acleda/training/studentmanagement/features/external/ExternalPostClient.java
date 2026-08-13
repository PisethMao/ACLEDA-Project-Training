package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExternalPostClient {
    private final WebClient jsonPlaceholderWebClient;
    private final ExternalPageMapper externalPageMapper;

    public Page<ExternalPostResponse> getPosts(
            Pageable pageable
    ) {
        ResponseEntity<List<ExternalPostResponse>> response =
                jsonPlaceholderWebClient
                        .get()
                        .uri(
                                uriBuilder -> uriBuilder
                                        .path("/posts")
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
                                        List<ExternalPostResponse>
                                        >() {
                                }
                        )
                        .block();
        Objects.requireNonNull(
                response,
                "External posts response must not be null"
        );
        return externalPageMapper
                .toPage(
                        response,
                        pageable
                );
    }

    public ExternalPostResponse getPost(
            Long postId
    ) {
        return jsonPlaceholderWebClient
                .get()
                .uri(
                        "/posts/{postId}",
                        postId
                )
                .retrieve()
                .bodyToMono(
                        ExternalPostResponse.class
                )
                .block();
    }

    public Page<ExternalPostResponse> getPostsByUser(
            Long userId,
            Pageable pageable
    ) {
        ResponseEntity<List<ExternalPostResponse>> response =
                jsonPlaceholderWebClient
                        .get()
                        .uri(
                                uriBuilder -> uriBuilder
                                        .path("/posts")
                                        .queryParam(
                                                "userId",
                                                userId
                                        )
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
                                        List<ExternalPostResponse>
                                        >() {
                                }
                        )
                        .block();
        Objects.requireNonNull(
                response,
                "External user posts response must not be null"
        );
        return externalPageMapper
                .toPage(
                        response,
                        pageable
                );
    }
}