package com.acleda.training.studentmanagement.security;

import com.acleda.training.studentmanagement.features.auth.CustomUserDetailsService;
import com.acleda.training.studentmanagement.features.auth.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    ) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/scalar",
                                "/scalar/**"
                        )
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh"
                        )
                        .permitAll()
                        .requestMatchers("/error")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/logout"
                        )
                        .hasAnyRole(
                                "USER",
                                "ADMIN"
                        )
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/departments/**",
                                "/api/v1/students/**",
                                "/api/v1/courses/**",
                                "/api/v1/external/**",
                                "/api/v1/enrollments/**",
                                "/api/v1/instructors/**",
                                "/api/v1/course-offerings/**"
                        )
                        .hasAnyRole(
                                "USER",
                                "ADMIN"
                        )
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/departments/**",
                                "/api/v1/students/**",
                                "/api/v1/courses/**",
                                "/api/v1/enrollments/**",
                                "/api/v1/instructors/**",
                                "/api/v1/course-offerings/**"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/departments/**",
                                "/api/v1/students/**",
                                "/api/v1/courses/**",
                                "/api/v1/enrollments/**",
                                "/api/v1/instructors/**",
                                "/api/v1/course-offerings/**"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/departments/**",
                                "/api/v1/students/**",
                                "/api/v1/courses/**",
                                "/api/v1/enrollments/**",
                                "/api/v1/instructors/**",
                                "/api/v1/course-offerings/**"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/departments/**",
                                "/api/v1/students/**",
                                "/api/v1/courses/**",
                                "/api/v1/enrollments/**",
                                "/api/v1/instructors/**",
                                "/api/v1/course-offerings/**"
                        )
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (_, response, _) -> {
                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.getWriter().write(
                                            """
                                                    {
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "A valid JWT access token is required"
                                                    }
                                                    """
                                    );
                                }
                        )
                        .accessDeniedHandler(
                                (_, response, _) -> {
                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.getWriter().write(
                                            """
                                                    {
                                                      "status": 403,
                                                      "error": "Forbidden",
                                                      "message": "You do not have permission to access this resource"
                                                    }
                                                    """
                                    );
                                }
                        )
                )
                .formLogin(
                        AbstractHttpConfigurer::disable
                )
                .httpBasic(
                        AbstractHttpConfigurer::disable
                )
                .logout(
                        AbstractHttpConfigurer::disable
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );
        provider.setPasswordEncoder(
                passwordEncoder
        );
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) {
        return configuration
                .getAuthenticationManager();
    }
}