package com.acleda.training.studentmanagement.features.auth;

import com.acleda.training.studentmanagement.exception.ApiErrorResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.auth.dto.AuthResponse;
import com.acleda.training.studentmanagement.features.auth.dto.LoginRequest;
import com.acleda.training.studentmanagement.features.auth.dto.RefreshTokenRequest;
import com.acleda.training.studentmanagement.features.auth.dto.RegisterRequest;
import com.acleda.training.studentmanagement.features.auth.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "APIs for user registration, login, token refresh, and logout"
)
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register user",
            description = "Creates a new user account"
    )
    @PostMapping("/register")
    public ResponseEntity<ApiErrorResponse<UserResponse>> register(
            @Valid
            @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest
    ) {
        UserResponse response =
                authService.register(request);
        return ResponseUtil.success(
                HttpStatus.CREATED,
                "User registered successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns access and refresh tokens"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiErrorResponse<AuthResponse>> login(
            @Valid
            @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        AuthResponse response =
                authService.login(request);
        return ResponseUtil.success(
                HttpStatus.OK,
                "Login successful",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiErrorResponse<AuthResponse>> refreshToken(
            @Valid
            @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpServletRequest
    ) {
        AuthResponse response =
                authService.refreshToken(request);
        return ResponseUtil.success(
                HttpStatus.OK,
                "Token refreshed successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Logout user",
            description = "Logs out the authenticated user and invalidates the current access token",
            security = @SecurityRequirement(
                    name = "bearerAuth"
            )
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiErrorResponse<Void>> logout(
            HttpServletRequest httpServletRequest
    ) {
        String authorizationHeader =
                httpServletRequest.getHeader(
                        HttpHeaders.AUTHORIZATION
                );
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException(
                    "Invalid Authorization header"
            );
        }
        String token =
                authorizationHeader.substring(7);
        authService.logout(token);
        return ResponseUtil.success(
                HttpStatus.OK,
                "Logout successful",
                null,
                httpServletRequest.getRequestURI()
        );
    }
}