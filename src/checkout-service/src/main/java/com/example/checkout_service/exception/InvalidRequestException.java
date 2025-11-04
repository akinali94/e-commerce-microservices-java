package com.example.checkout_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for general invalid request parameters.
 */
public class InvalidRequestException extends ServiceException {
    public InvalidRequestException(String message) {
        super(message, "INVALID_REQUEST", HttpStatus.BAD_REQUEST.value());
    }
}