package com.istad.docuhub.feature.adviserDetail.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Builder
public record AdviserDetailRequest(

        @NotNull(message = "Experience years is required")
        @Min(value = 1, message = "Experience must be at least 1 year")
        @Max(value = 60, message = "Experience cannot exceed 60 years")
        Integer experienceYears,

        @NotBlank(message = "LinkedIn URL is required")
        @Pattern(
                regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*$",
                message = "Invalid LinkedIn profile URL"
        )
        String linkedinUrl,

        @NotBlank(message = "Office is required")
        @Size(max = 500, message = "office must not exceed 500 characters")
        String office,

        @NotBlank(message = "Social links are required")
        @Size(max = 500, message = "Social links must not exceed 500 characters")
        String socialLinks,

        @NotBlank(message = "User UUID is required")
        @Pattern(
                regexp = "^[0-9a-fA-F-]{36}$",
                message = "Invalid UUID format for userUuid"
        )
        String userUuid,

        @NotEmpty(message = "At least one specialization is required")
        List<
                @Pattern(
                        regexp = "^[0-9a-fA-F-]{36}$",
                        message = "Invalid UUID format for specializeUuid"
                ) String
                > specializeUuids

) {}
