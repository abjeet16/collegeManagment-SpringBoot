package com.mynotes.services;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    // Add token to blacklist with its expiration time
    public void blacklistToken(String token, long expirationTime) {
        blacklist.put(token, expirationTime);
    }

    // Check if the token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}

