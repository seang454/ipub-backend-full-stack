package com.istad.docuhub.feature.adviserAssignment.dto;


public record ApiResponse<T>(
        String status,
        T data
) {}

