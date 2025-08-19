package com.istad.docuhub.feature.user.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserCreateDto(
        String username,
        String email,
        String firstname,
        String lastname,
        String password,
        String confirmedPassword
) {
}
