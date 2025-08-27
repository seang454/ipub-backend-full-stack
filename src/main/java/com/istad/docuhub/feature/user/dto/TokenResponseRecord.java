package com.istad.docuhub.feature.user.dto;
import java.util.Map;

public record TokenResponseRecord(
        String accessToken,
        String refreshToken,
        String idToken,
        Map<String, Object> claims
) {}

