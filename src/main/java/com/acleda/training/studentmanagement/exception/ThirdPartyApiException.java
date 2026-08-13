package com.acleda.training.studentmanagement.exception;

import lombok.Getter;

@Getter
public class ThirdPartyApiException extends RuntimeException {
    private final Integer statusCode;
    private final String responseBody;

    public ThirdPartyApiException(
            String message,
            Integer statusCode,
            String responseBody
    ) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}