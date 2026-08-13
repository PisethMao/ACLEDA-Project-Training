package com.acleda.training.studentmanagement.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter
        extends OncePerRequestFilter {
    private static final String REQUEST_ID =
            "requestId";
    private static final String REQUEST_ID_HEADER =
            "X-Request-ID";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId =
                UUID.randomUUID().toString();
        try {
            MDC.put(
                    REQUEST_ID,
                    requestId
            );
            response.setHeader(
                    REQUEST_ID_HEADER,
                    requestId
            );
            filterChain.doFilter(
                    request,
                    response
            );
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }
}