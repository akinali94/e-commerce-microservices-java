package com.example.ad_service.exception;

import org.springframework.http.HttpStatus;


public class AdServiceException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String code;

    public AdServiceException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public AdServiceException(String message, HttpStatus statusCode) {
        this(message, statusCode, null);
    }

    public AdServiceException(String message, HttpStatus statusCode, String code) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
    }

    public AdServiceException(String message, Throwable cause) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR, null, cause);
    }

    public AdServiceException(String message, HttpStatus statusCode, String code, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.code = code;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }
}