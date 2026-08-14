package com.acleda.training.studentmanagement.config;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.integration.Slf4jThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class ReactorMdcConfig {
    private static final String REQUEST_ID = "requestId";

    @PostConstruct
    void configureContextPropagation() {
        ContextRegistry
                .getInstance()
                .registerThreadLocalAccessor(
                        new Slf4jThreadLocalAccessor(
                                REQUEST_ID
                        )
                );
        Hooks.enableAutomaticContextPropagation();
    }
}