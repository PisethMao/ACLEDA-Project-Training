package com.acleda.training.studentmanagement.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ThirdPartyErrorHandler {
    public Mono<? extends Throwable> handle(
            ClientResponse response
    ) {
        int statusCode =
                response
                        .statusCode()
                        .value();
        return response
                .bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> {
                    log.error(
                            "[THIRD-PARTY ERROR] status={} body={}",
                            statusCode,
                            body
                    );
                    return new ThirdPartyApiException(
                            "Third-party API returned HTTP "
                                    + statusCode,
                            statusCode,
                            body
                    );
                });
    }
}