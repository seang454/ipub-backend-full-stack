package com.istad.docuhub.feature.user;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();
    // Replace with DB or Redis in production

    public void storeToken(String username, String refreshToken) {
        refreshTokenStore.put(username, refreshToken);
    }

    public String getToken(String username) {
        return refreshTokenStore.get(username);
    }

    public void removeToken(String username) {
        refreshTokenStore.remove(username);
    }
}

