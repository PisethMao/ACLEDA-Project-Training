package com.acleda.training.studentmanagement.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ApiLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(
                        request,
                        1024 * 1024
                );
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(
                        response
                );
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(
                    requestWrapper,
                    responseWrapper
            );
        } catch (Exception ex) {
            log.error(
                    "API ERROR | Method: {} | URI: {} | Exception: {} | Message: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        } finally {
            long duration =
                    System.currentTimeMillis() - startTime;
            String requestBody =
                    getRequestBody(requestWrapper);
            String responseBody =
                    getResponseBody(
                            responseWrapper,
                            request.getRequestURI()
                    );
            log.info("TEST LOG LINE");
            log.info(
                    "API REQUEST | Method: {} | URI: {} | Query: {} | Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    requestBody
            );
            log.info(
                    "API RESPONSE | Method: {} | URI: {} | Status: {} | Duration: {}ms | Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    responseBody
            );
            responseWrapper.copyBodyToResponse();
        }
    }

    private String getRequestBody(
            ContentCachingRequestWrapper request
    ) {
        if (isSensitiveEndpoint(request.getRequestURI())) {
            return "[HIDDEN]";
        }
        byte[] content =
                request.getContentAsByteArray();
        if (content.length == 0) {
            return "EMPTY";
        }
        return new String(
                content,
                StandardCharsets.UTF_8
        );
    }

    private String getResponseBody(
            ContentCachingResponseWrapper response,
            String uri
    ) {
        if (isSensitiveEndpoint(uri)) {
            return "[HIDDEN]";
        }
        byte[] content =
                response.getContentAsByteArray();
        if (content.length == 0) {
            return "EMPTY";
        }
        return new String(
                content,
                StandardCharsets.UTF_8
        );
    }

    private boolean isSensitiveEndpoint(
            String uri
    ) {
        return uri.startsWith("/api/v1/auth");
    }
}