package com.istad.docuhub.feature.specialize.dto;

import jakarta.validation.constraints.NotBlank;

public record SpecializeRequest(
        @NotBlank
        String name,
        String slug
) {
}