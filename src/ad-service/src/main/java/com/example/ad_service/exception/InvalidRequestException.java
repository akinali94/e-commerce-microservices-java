package com.example.ad_service.exception;


import org.springframework.http.HttpStatus;


public class InvalidRequestException extends AdServiceException {
    
    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_REQUEST");
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_REQUEST", cause);
    }
}