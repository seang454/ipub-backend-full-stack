package com.istad.docuhub.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ExceptionResponse<T>(
        String message,
        Integer status,
        LocalDateTime timestamp,
        T detail
) {
}
