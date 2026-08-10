package com.acleda.training.studentmanagement.features.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {
    private final PrivateKey privateKey;
    private final JwtParser jwtParser;
    private final String issuer;
    @Getter
    private final long expirationSeconds;
    @Getter
    private final long refreshExpirationSeconds;

    public JwtService(
            @Value("${security.jwt.private-key-path}")
            String privateKeyPath,
            @Value("${security.jwt.public-key-path}")
            String publicKeyPath,
            @Value("${security.jwt.issuer}")
            String issuer,
            @Value("${security.jwt.expiration-seconds}")
            long expirationSeconds,
            @Value("${security.jwt.refresh-expiration-seconds}")
            long refreshExpirationSeconds
    ) {
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException(
                    "JWT issuer must not be empty"
            );
        }
        if (expirationSeconds <= 0) {
            throw new IllegalStateException(
                    "JWT expiration must be greater than zero"
            );
        }
        if (refreshExpirationSeconds <= 0) {
            throw new IllegalStateException(
                    "Refresh token expiration must be greater than zero"
            );
        }
        PublicKey publicKey;
        try {
            String privatePem =
                    Files.readString(
                            Path.of(privateKeyPath)
                    );
            String publicPem =
                    Files.readString(
                            Path.of(publicKeyPath)
                    );
            this.privateKey =
                    loadPrivateKey(privatePem);
            publicKey =
                    loadPublicKey(publicPem);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to load RSA keys",
                    exception
            );
        }
        this.issuer = issuer;
        this.expirationSeconds =
                expirationSeconds;
        this.refreshExpirationSeconds =
                refreshExpirationSeconds;
        this.jwtParser =
                Jwts.parser()
                        .verifyWith(publicKey)
                        .requireIssuer(issuer)
                        .build();
    }

    public String generateAccessToken(
            Authentication authentication
    ) {
        return generateToken(
                authentication.getName(),
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .toList(),
                expirationSeconds,
                "access"
        );
    }

    public String generateRefreshToken(
            Authentication authentication
    ) {
        return generateToken(
                authentication.getName(),
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .toList(),
                refreshExpirationSeconds,
                "refresh"
        );
    }

    public String generateAccessToken(
            UserDetails userDetails
    ) {
        return generateToken(
                userDetails.getUsername(),
                userDetails
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .toList(),
                expirationSeconds,
                "access"
        );
    }

    private PrivateKey loadPrivateKey(
            String pem
    ) throws Exception {
        String key =
                pem.replace(
                                "-----BEGIN PRIVATE KEY-----",
                                ""
                        )
                        .replace(
                                "-----END PRIVATE KEY-----",
                                ""
                        )
                        .replaceAll(
                                "\\s",
                                ""
                        );
        byte[] decoded =
                Base64.getDecoder()
                        .decode(key);
        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(
                        decoded
                );
        KeyFactory keyFactory =
                KeyFactory.getInstance(
                        "RSA"
                );
        return keyFactory.generatePrivate(
                keySpec
        );
    }

    private PublicKey loadPublicKey(
            String pem
    ) throws Exception {
        String key =
                pem.replace(
                                "-----BEGIN PUBLIC KEY-----",
                                ""
                        )
                        .replace(
                                "-----END PUBLIC KEY-----",
                                ""
                        )
                        .replaceAll(
                                "\\s",
                                ""
                        );
        byte[] decoded =
                Base64.getDecoder()
                        .decode(key);
        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(
                        decoded
                );
        KeyFactory keyFactory =
                KeyFactory.getInstance(
                        "RSA"
                );
        return keyFactory.generatePublic(
                keySpec
        );
    }

    private String generateToken(
            String username,
            List<String> roles,
            long expiration,
            String tokenType
    ) {
        Instant issuedAt = Instant.now();
        Instant expiresAt =
                issuedAt.plusSeconds(expiration);
        String sessionId =
                UUID.randomUUID().toString();
        List<String> permissions = List.of(
                "student:read",
                "student:create",
                "student:update",
                "student:delete",
                "department:read",
                "department:create",
                "department:update",
                "department:delete",
                "course:read",
                "course:create",
                "course:update",
                "course:delete",
                "enrollment:read",
                "enrollment:create",
                "enrollment:update",
                "enrollment:delete"
        );
        List<String> scopes = List.of(
                "openid",
                "profile",
                "student",
                "department",
                "course",
                "enrollment"
        );
        return Jwts.builder()
                .header()
                .type("JWT")
                .keyId("student-management-key-1")
                .and()
                .id(
                        UUID.randomUUID()
                                .toString()
                )
                .issuer(
                        issuer
                )
                .subject(
                        username
                )
                .audience()
                .add(
                        "student-management-client"
                )
                .and()
                .issuedAt(
                        Date.from(issuedAt)
                )
                .expiration(
                        Date.from(expiresAt)
                )
                .claim(
                        "token_type",
                        tokenType
                )
                .claim(
                        "client_id",
                        "student-management-web"
                )
                .claim(
                        "session_id",
                        sessionId
                )
                .claim(
                        "auth_time",
                        issuedAt.getEpochSecond()
                )
                .claim(
                        "authentication_method",
                        "password"
                )
                .claim(
                        "roles",
                        roles
                )
                .claim(
                        "authorities",
                        roles
                )
                .claim(
                        "scope",
                        scopes
                )
                .claim(
                        "permissions",
                        permissions
                )
                .claim(
                        "resource_access",
                        Map.of(
                                "student-management-api",
                                Map.of(
                                        "roles",
                                        roles,
                                        "permissions",
                                        permissions
                                )
                        )
                )
                .claim(
                        "application",
                        Map.of(
                                "name",
                                "Student Management API",
                                "client",
                                "student-management-web",
                                "version",
                                "v1",
                                "environment",
                                "development"
                        )
                )
                .claim(
                        "features",
                        List.of(
                                "STUDENT_MANAGEMENT",
                                "DEPARTMENT_MANAGEMENT",
                                "COURSE_MANAGEMENT",
                                "ENROLLMENT_MANAGEMENT",
                                "JWT_AUTHENTICATION",
                                "ROLE_BASED_ACCESS_CONTROL"
                        )
                )
                .signWith(
                        privateKey,
                        Jwts.SIG.RS256
                )
                .compact();
    }

    public String extractUsername(
            String token
    ) {
        return extractAllClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        Claims claims =
                extractAllClaims(token);
        String username =
                claims.getSubject();
        String tokenType =
                claims.get(
                        "token_type",
                        String.class
                );
        return username.equalsIgnoreCase(
                userDetails.getUsername()
        )
                && userDetails.isEnabled()
                && "access".equals(tokenType);
    }

    public boolean isRefreshTokenValid(
            String token,
            UserDetails userDetails
    ) {
        Claims claims =
                extractAllClaims(token);
        String username =
                claims.getSubject();
        String tokenType =
                claims.get(
                        "token_type",
                        String.class
                );
        return username.equalsIgnoreCase(
                userDetails.getUsername()
        )
                && userDetails.isEnabled()
                && "refresh".equals(tokenType);
    }

    private Claims extractAllClaims(
            String token
    ) {
        return jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }
}