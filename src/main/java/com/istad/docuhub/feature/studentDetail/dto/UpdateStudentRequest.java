package com.istad.docuhub.feature.studentDetail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStudentRequest(
        @NotBlank(message = "Student card URL cannot be empty")
        String studentCardUrl,

        @NotBlank(message = "University cannot be empty")
        String university,

        @NotBlank(message = "Major cannot be empty")
        String major,

        @NotBlank(message = "Years of study cannot be empty")
        @Size(max = 10, message = "Years of study must be at most 10 characters")
        String yearsOfStudy
) {}
