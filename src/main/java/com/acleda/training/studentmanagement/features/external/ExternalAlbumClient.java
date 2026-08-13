package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.features.external.dto.ExternalAlbumResponse;
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
public class ExternalAlbumClient {
    private final WebClient jsonPlaceholderWebClient;
    private final ExternalPageMapper externalPageMapper;

    public Page<ExternalAlbumResponse> getAlbums(
            Pageable pageable
    ) {
        ResponseEntity<List<ExternalAlbumResponse>> response =
                jsonPlaceholderWebClient
                        .get()
                        .uri(
                                uriBuilder -> uriBuilder
                                        .path("/albums")
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
                                        List<ExternalAlbumResponse>
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

    public ExternalAlbumResponse getAlbum(
            Long albumId
    ) {
        return jsonPlaceholderWebClient
                .get()
                .uri(
                        "/albums/{albumId}",
                        albumId
                )
                .retrieve()
                .bodyToMono(
                        ExternalAlbumResponse.class
                )
                .block();
    }

    public Page<ExternalAlbumResponse> getAlbumsByUser(
            Long userId,
            Pageable pageable
    ) {
        ResponseEntity<List<ExternalAlbumResponse>> response =
                jsonPlaceholderWebClient
                        .get()
                        .uri(
                                uriBuilder -> uriBuilder
                                        .path("/albums")
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
                                        List<ExternalAlbumResponse>
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