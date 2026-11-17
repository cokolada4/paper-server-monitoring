package com.mscc.bridge.security;

import java.security.MessageDigest;

public class TokenManager {
    private final String expectedToken;

    public TokenManager(String token) {
        this.expectedToken = token != null ? token : "";
    }

    public boolean validateToken(String providedToken) {
        if (providedToken == null || expectedToken.isEmpty()) {
            return false;
        }
        // Constant-time comparison to prevent timing attacks
        return MessageDigest.isEqual(expectedToken.getBytes(), providedToken.getBytes());
    }
}
