package com.acleda.training.studentmanagement.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ResponseLoggingAdvice
        implements ResponseBodyAdvice<Object> {
    private static final int MAX_BODY_LENGTH = 5000;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class converterType
    ) {
        return true;
    }
    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        String path =
                request
                        .getURI()
                        .getPath();
        try {
            String responseBody =
                    objectMapper
                            .writerWithDefaultPrettyPrinter()
                            .writeValueAsString(body);
            log.info(
                    "API RESPONSE BODY | Method: {} | URI: {} | Body:\n{}",
                    request.getMethod(),
                    path,
                    truncate(responseBody)
            );
        } catch (Exception e) {
            log.warn(
                    "Failed to log API response body | Method: {} | URI: {}",
                    request.getMethod(),
                    path,
                    e
            );
        }
        return body;
    }
    private String truncate(
            String body
    ) {
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