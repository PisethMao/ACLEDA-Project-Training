package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalCommentResponse;
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
public class ExternalCommentClient {
    private final WebClient jsonPlaceholderWebClient;
    private final ExternalPageMapper externalPageMapper;

    public Page<ExternalCommentResponse> getComments(
            Pageable pageable
    ) {
        ResponseEntity<List<ExternalCommentResponse>> response =
                jsonPlaceholderWebClient
                        .get()
                        .uri(
                                uriBuilder -> uriBuilder
                                        .path("/comments")
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
                                        List<ExternalCommentResponse>
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

    public ExternalCommentResponse getComment(
            Long commentId
    ) {
        return jsonPlaceholderWebClient
                .get()
                .uri(
                        "/comments/{commentId}",
                        commentId
                )
                .retrieve()
                .bodyToMono(
                        ExternalCommentResponse.class
                )
                .block();
    }

    public Page<ExternalCommentResponse> getCommentsByPost(
            Long postId,
            Pageable pageable
    ) {
        ResponseEntity<List<ExternalCommentResponse>> response =
                jsonPlaceholderWebClient
                        .get()
                        .uri(
                                uriBuilder -> uriBuilder
                                        .path("/comments")
                                        .queryParam(
                                                "postId",
                                                postId
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
                                        List<ExternalCommentResponse>
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
}