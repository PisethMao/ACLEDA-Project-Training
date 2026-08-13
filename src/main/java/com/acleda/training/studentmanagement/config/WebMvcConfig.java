package com.acleda.training.studentmanagement.config;

import com.acleda.training.studentmanagement.logging.RequestLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig
        implements WebMvcConfigurer {
    private final RequestLoggingInterceptor
            requestLoggingInterceptor;

    @Override
    public void addInterceptors(
            @NonNull InterceptorRegistry registry
    ) {
        registry.addInterceptor(
                requestLoggingInterceptor
        );
    }
}