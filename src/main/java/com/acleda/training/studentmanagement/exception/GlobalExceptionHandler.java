package com.acleda.training.studentmanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "RESOURCE NOT FOUND | Method: {} | URI: {} | Message: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );
        return ResponseUtil.fail(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleEndpointNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "ENDPOINT NOT FOUND | Method: {} | URI: {} | Message: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );
        return ResponseUtil.fail(
                HttpStatus.NOT_FOUND,
                "No endpoint found for "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "BAD REQUEST | Method: {} | URI: {} | Message: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );
        return ResponseUtil.fail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors =
                new LinkedHashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );
        log.warn(
                "VALIDATION FAILED | Method: {} | URI: {} | Errors: {}",
                request.getMethod(),
                request.getRequestURI(),
                validationErrors
        );
        return ResponseUtil.fail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                validationErrors,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleInvalidJson(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "INVALID JSON | Method: {} | URI: {} | Exception: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );
        return ResponseUtil.fail(
                HttpStatus.BAD_REQUEST,
                "Request body is invalid or malformed",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String message;
        if (exception.getRequiredType() != null) {
            message = "Invalid value '"
                    + exception.getValue()
                    + "' for parameter '"
                    + exception.getName()
                    + "'. Expected type: "
                    + exception.getRequiredType().getSimpleName();
        } else {
            message = "Invalid value for parameter '"
                    + exception.getName()
                    + "'";
        }
        log.warn(
                "TYPE MISMATCH | Method: {} | URI: {} | Message: {}",
                request.getMethod(),
                request.getRequestURI(),
                message
        );
        return ResponseUtil.fail(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleParameterValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors =
                new LinkedHashMap<>();
        exception.getParameterValidationResults()
                .forEach(result -> {
                    String parameterName =
                            result.getMethodParameter()
                                    .getParameterName();
                    result.getResolvableErrors()
                            .forEach(error ->
                                    errors.put(
                                            parameterName != null
                                                    ? parameterName
                                                    : "parameter",
                                            error.getDefaultMessage()
                                    )
                            );
                });
        log.warn(
                "PARAMETER VALIDATION FAILED | Method: {} | URI: {} | Errors: {}",
                request.getMethod(),
                request.getRequestURI(),
                errors
        );
        return ResponseUtil.fail(
                HttpStatus.BAD_REQUEST,
                "Parameter validation failed",
                errors,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "CONFLICT | Method: {} | URI: {} | Message: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );
        return ResponseUtil.fail(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "DATA INTEGRITY VIOLATION | Method: {} | URI: {} | Exception: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );
        return ResponseUtil.fail(
                HttpStatus.CONFLICT,
                "The provided data conflicts with an existing record",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "AUTHENTICATION FAILED | Method: {} | URI: {} | Exception: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );
        return ResponseUtil.fail(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "METHOD NOT ALLOWED | Method: {} | URI: {} | Exception: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );
        return ResponseUtil.fail(
                HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method "
                        + request.getMethod()
                        + " is not supported for "
                        + request.getRequestURI(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse<Object>> handleInternalServerError(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error(
                "INTERNAL SERVER ERROR | Method: {} | URI: {} | Exception: {} | Message: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                exception
        );
        return ResponseUtil.fail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal server error occurred",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ThirdPartyApiException.class)
    public ResponseEntity<ApiErrorResponse<Object>>
    handleThirdPartyApiException(
            ThirdPartyApiException exception,
            HttpServletRequest request
    ) {
        log.error(
                "Third-party API error: {}",
                exception.getMessage()
        );
        return ResponseUtil.fail(
                HttpStatus.BAD_GATEWAY,
                exception.getMessage(),
                request.getRequestURI()
        );
    }
}