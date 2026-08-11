package com.acleda.training.studentmanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfig {
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(
                request -> {
                    log.info(
                            "[THIRD-PARTY REQUEST] method={} url={}",
                            request.method(),
                            request.url()
                    );
                    return Mono.just(request);
                }
        );
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(
                response -> {
                    log.info(
                            "[THIRD-PARTY RESPONSE] status={}",
                            response.statusCode().value()
                    );
                    return Mono.just(response);
                }
        );
    }

    @Bean
    public WebClient jsonPlaceholderWebClient(
            @Value("${third-party.json-placeholder.base-url}")
            String baseUrl
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }
}