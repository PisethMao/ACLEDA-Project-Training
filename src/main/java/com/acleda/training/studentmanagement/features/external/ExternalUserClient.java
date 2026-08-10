package com.acleda.training.studentmanagement.features.external;

import com.acleda.training.studentmanagement.exception.ThirdPartyApiException;
import com.acleda.training.studentmanagement.features.external.dto.ExternalUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalUserClient {
    private final WebClient jsonPlaceholderWebClient;

    public ExternalUserResponse getUser(Long userId) {
        log.info(
                "[THIRD-PARTY CALL] Fetching external user id={}",
                userId
        );
        return jsonPlaceholderWebClient
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path("/users/{id}")
                                .build(userId)
                )
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response
                                .bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    log.error(
                                            "[THIRD-PARTY ERROR RESPONSE] status={} body={}",
                                            response.statusCode().value(),
                                            body
                                    );
                                    return reactor.core.publisher.Mono.error(
                                            new ThirdPartyApiException(
                                                    "Third-party API returned status "
                                                            + response.statusCode().value()
                                            )
                                    );
                                })
                )
                .bodyToMono(ExternalUserResponse.class)
                .doOnNext(response ->
                        log.info(
                                "[THIRD-PARTY RESPONSE BODY] {}",
                                response
                        )
                )
                .doOnError(exception ->
                        log.error(
                                "[THIRD-PARTY CALL FAILED] userId={} message={}",
                                userId,
                                exception.getMessage()
                        )
                )
                .block();
    }
}