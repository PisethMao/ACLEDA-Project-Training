package com.acleda.training.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public final class ResponseUtil {
    private ResponseUtil() {
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(
            HttpStatus status,
            String message,
            T data,
            String path
    ) {
        ApiResponse<T> response =
                ApiResponse.<T>builder()
                        .success(true)
                        .statusCode(status.value())
                        .message(message)
                        .data(data)
                        .path(path)
                        .timestamp(Instant.now())
                        .build();
        return ResponseEntity
                .status(status)
                .body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(
            HttpStatus status,
            String message,
            String path
    ) {
        ApiResponse<T> response =
                ApiResponse.<T>builder()
                        .success(false)
                        .statusCode(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(path)
                        .timestamp(Instant.now())
                        .build();
        return ResponseEntity
                .status(status)
                .body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(
            HttpStatus status,
            String message,
            T data,
            String path
    ) {
        ApiResponse<T> response =
                ApiResponse.<T>builder()
                        .success(false)
                        .statusCode(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .data(data)
                        .path(path)
                        .timestamp(Instant.now())
                        .build();
        return ResponseEntity
                .status(status)
                .body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(
            HttpStatus status,
            String error,
            String message,
            String path
    ) {
        ApiResponse<T> response =
                ApiResponse.<T>builder()
                        .success(false)
                        .statusCode(status.value())
                        .error(error)
                        .message(message)
                        .path(path)
                        .timestamp(Instant.now())
                        .build();
        return ResponseEntity
                .status(status)
                .body(response);
    }
}