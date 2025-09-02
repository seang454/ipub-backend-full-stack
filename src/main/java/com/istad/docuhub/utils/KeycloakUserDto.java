package com.istad.docuhub.utils;

public record KeycloakUserDto(
        String id,
        String username,
        String email
) {
}
