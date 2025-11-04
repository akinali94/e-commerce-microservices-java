package com.example.checkout_service.exception;

/**
 * Base exception for service-related errors.
 */
public class ServiceException extends RuntimeException {
    private final int statusCode;
    private final String errorType;
    
    public ServiceException(String message, String errorType, int statusCode) {
        super(message);
        this.errorType = errorType;
        this.statusCode = statusCode;
    }
    
    public ServiceException(String message, String errorType, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getErrorType() {
        return errorType;
    }
}