package com.istad.docuhub.feature.user;

import com.istad.docuhub.domain.RefreshTokenEntity;
import com.istad.docuhub.feature.user.mapper.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public void storeToken(String username, String refreshToken, long ttlSeconds) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUsername(username);
        entity.setToken(refreshToken);
        entity.setIssuedAt(Instant.now());
        entity.setExpiresAt(Instant.now().plusSeconds(ttlSeconds));
        repository.save(entity);
    }

    public String getToken(String username) {
        if (username==null || username.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username is required");
        }
        return repository.findByUsername(username)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .map(RefreshTokenEntity::getToken)
                .orElse(null);
    }

    public void removeToken(String username) {
        repository.deleteByUsername(username);
    }
}


