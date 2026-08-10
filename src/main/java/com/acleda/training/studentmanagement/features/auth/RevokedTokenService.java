package com.acleda.training.studentmanagement.features.auth;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RevokedTokenService {
    private final Set<String> revokedTokens =
            ConcurrentHashMap.newKeySet();

    public void revokeToken(String token) {
        revokedTokens.add(token);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }
}