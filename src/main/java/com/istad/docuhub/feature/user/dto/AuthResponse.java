package com.istad.docuhub.feature.user.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}

