package com.acleda.training.studentmanagement.features.auth;

import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.features.auth.dto.*;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RevokedTokenService revokedTokenService;

    private String normalizeUsername(
            String username
    ) {
        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    @Transactional
    @CachePut(
            value = "register",
            key = "#result.id()"
    )
    public UserResponse register(
            RegisterRequest request
    ) {
        String username =
                normalizeUsername(
                        request.username()
                );
        if (appUserRepository
                .existsByUsernameIgnoreCase(username)) {
            throw new ConflictException(
                    "Username " +
                            username +
                            " already exists"
            );
        }
        // Use By Mapper
        AppUser user =
                appUserMapper.toEntity(request);
        user.setUsername(username);
        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        AppUser savedUser =
                appUserRepository
                        .saveAndFlush(user);
        return appUserMapper
                .toResponse(savedUser);
        // Use By Manual
//        AppUser user = new AppUser();
//        user.setUsername(username);
//        user.setPassword(
//                passwordEncoder.encode(
//                        request.password()
//                )
//        );
//        user.setRole(UserRole.USER);
//        user.setEnabled(true);
//        AppUser savedUser =
//                appUserRepository
//                        .saveAndFlush(user);
//        return new UserResponse(
//                savedUser.getId(),
//                savedUser.getUsername(),
//                savedUser.getRole(),
//                savedUser.isEnabled(),
//                savedUser.getCreatedAt(),
//                savedUser.getUpdatedAt()
//        );
    }

    @Transactional(readOnly = true)
    @CachePut(
            value = "login",
            key = "#result.username()"
    )
    public AuthResponse login(
            LoginRequest request
    ) {
        String username =
                normalizeUsername(
                        request.username()
                );
        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    request.password()
                            )
                    );
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }
        String accessToken =
                jwtService.generateAccessToken(
                        authentication
                );
        String refreshToken =
                jwtService.generateRefreshToken(
                        authentication
                );
        List<String> roles =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .toList();
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                jwtService.getRefreshExpirationSeconds(),
                authentication.getName(),
                roles
        );
    }

    @Transactional(readOnly = true)
    @CachePut(
            value = "refeshToken",
            key = "#result.username()"
    )
    public AuthResponse refreshToken(
            RefreshTokenRequest request
    ) {
        try {
            String username =
                    jwtService.extractUsername(
                            request.refreshToken()
                    );
            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(
                                    username
                            );
            if (!jwtService.isRefreshTokenValid(
                    request.refreshToken(),
                    userDetails
            )) {
                throw new BadCredentialsException(
                        "Invalid refresh token"
                );
            }
            String accessToken =
                    jwtService.generateAccessToken(
                            userDetails
                    );
            List<String> roles =
                    userDetails
                            .getAuthorities()
                            .stream()
                            .map(
                                    GrantedAuthority::getAuthority
                            )
                            .toList();
            return new AuthResponse(
                    accessToken,
                    request.refreshToken(),
                    "Bearer",
                    jwtService.getExpirationSeconds(),
                    jwtService.getRefreshExpirationSeconds(),
                    userDetails.getUsername(),
                    roles
            );
        } catch (JwtException exception) {
            throw new BadCredentialsException(
                    "Invalid or expired refresh token"
            );
        }
    }

    public void logout(String token) {
        revokedTokenService.revokeToken(token);
        SecurityContextHolder.clearContext();
    }
}