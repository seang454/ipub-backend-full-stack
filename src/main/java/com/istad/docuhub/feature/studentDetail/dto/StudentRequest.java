package com.istad.docuhub.feature.studentDetail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentRequest(
        @NotBlank(message = "Student card URL is required")
        String studentCardUrl,

        @NotBlank(message = "University is required")
        String university,

        @NotBlank(message = "Major is required")
        String major,

        @NotBlank(message = "Years of study is required")
        @Size(max = 10, message = "Years of study must be at most 10 characters")
        String yearsOfStudy,

        @NotNull(message = "User UUID is required")
        String userUuid
) {}
