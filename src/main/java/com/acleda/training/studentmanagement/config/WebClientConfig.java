package com.acleda.training.studentmanagement.config;

import com.acleda.training.studentmanagement.exception.ThirdPartyErrorHandler;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        JsonPlaceholderProperties.class
)
public class WebClientConfig {
    private static final int MAX_BODY_LENGTH = 5000;

    @Bean
    public WebClient jsonPlaceholderWebClient(
            WebClient.Builder builder,
            JsonPlaceholderProperties properties,
            ThirdPartyErrorHandler errorHandler
    ) {
        HttpClient httpClient =
                HttpClient
                        .create()
                        .option(
                                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                                Math.toIntExact(
                                        properties
                                                .connectTimeout()
                                                .toMillis()
                                )
                        )
                        .responseTimeout(
                                properties.responseTimeout()
                        );
        return builder
                .baseUrl(
                        properties.baseUrl()
                )
                .clientConnector(
                        new ReactorClientHttpConnector(
                                httpClient
                        )
                )
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        errorHandler::handle
                )
                .filter(
                        logThirdPartyExchange()
                )
                .build();
    }

    private ExchangeFilterFunction logThirdPartyExchange() {
        return (request, next) ->
                Mono.defer(() -> {
                    long startTime = System.nanoTime();
                    log.info(
                            "THIRD-PARTY REQUEST | Method: {} | URL: {}",
                            request.method(),
                            request.url()
                    );
                    return next
                            .exchange(request)
                            .flatMap(response -> {
                                long durationMs =
                                        TimeUnit.NANOSECONDS
                                                .toMillis(
                                                        System.nanoTime()
                                                                - startTime
                                                );
                                return response
                                        .bodyToMono(String.class)
                                        .defaultIfEmpty("")
                                        .map(body -> {
                                            log.info(
                                                    "THIRD-PARTY RESPONSE | Method: {} | URL: {} | Status: {} | Duration: {}ms",
                                                    request.method(),
                                                    request.url(),
                                                    response
                                                            .statusCode()
                                                            .value(),
                                                    durationMs
                                            );
                                            log.info(
                                                    "THIRD-PARTY RESPONSE BODY | Method: {} | URL: {} | Body: {}",
                                                    request.method(),
                                                    request.url(),
                                                    truncate(body)
                                            );
                                            return response
                                                    .mutate()
                                                    .body(body)
                                                    .build();
                                        });
                            })
                            .doOnError(exception -> {
                                long durationMs =
                                        TimeUnit.NANOSECONDS
                                                .toMillis(
                                                        System.nanoTime()
                                                                - startTime
                                                );
                                log.error(
                                        "THIRD-PARTY ERROR | Method: {} | URL: {} | Duration: {}ms | Error: {}",
                                        request.method(),
                                        request.url(),
                                        durationMs,
                                        exception.getMessage()
                                );
                            });
                });
    }

    private String truncate(String body) {
        if (body == null) {
            return null;
        }
        if (body.length() <= MAX_BODY_LENGTH) {
            return body;
        }
        return body.substring(
                0,
                MAX_BODY_LENGTH
        ) + "...[TRUNCATED]";
    }
}