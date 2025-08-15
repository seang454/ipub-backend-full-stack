package com.istad.docuhub.exception;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleServiceException(ResponseStatusException ex) {
        ExceptionResponse<String> errorResponse = ExceptionResponse.<String>builder()
                .message(" Error Logic In Service  ")
                .status(ex.getStatusCode().value())
                .timestamp(LocalDateTime.now())
                .detail(ex.getReason())
                .build();
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }
}
