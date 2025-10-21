package com.istad.docuhub.feature.studentDetail.dto;

import jakarta.validation.constraints.Size;

public record UpdateStudentRequest(

        // Optional field, validate only if not null
        @Size(max = 255, message = "Student card URL must be at most 255 characters")
        String studentCardUrl,

        @Size(max = 255, message = "University name must be at most 255 characters")
        String university,

        @Size(max = 255, message = "Major must be at most 255 characters")
        String major,

        @Size(max = 10, message = "Years of study must be at most 10 characters")
        String yearsOfStudy
) {}
