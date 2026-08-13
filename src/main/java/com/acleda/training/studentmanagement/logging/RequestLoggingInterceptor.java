package com.acleda.training.studentmanagement.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor
        implements HandlerInterceptor {
    private static final String START_TIME =
            "requestStartTime";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        request.setAttribute(
                START_TIME,
                System.nanoTime()
        );
        log.info(
                "API REQUEST | Method: {} | URI: {} | Query: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex
    ) {
        Object startTimeAttribute =
                request.getAttribute(START_TIME);
        long durationMs = 0;
        if (startTimeAttribute instanceof Long startTime) {
            durationMs =
                    (System.nanoTime() - startTime)
                            / 1_000_000;
        }
        log.info(
                "API RESPONSE | Method: {} | URI: {} | Status: {} | Duration: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                durationMs
        );
    }
}