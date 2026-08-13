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

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        JsonPlaceholderProperties.class
)
public class WebClientConfig {
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
                        logRequest()
                )
                .filter(
                        logResponse()
                )
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction
                .ofRequestProcessor(
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
        return ExchangeFilterFunction
                .ofResponseProcessor(
                        response -> {
                            log.info(
                                    "[THIRD-PARTY RESPONSE] status={}",
                                    response
                                            .statusCode()
                                            .value()
                            );
                            return Mono.just(response);
                        }
                );
    }
}