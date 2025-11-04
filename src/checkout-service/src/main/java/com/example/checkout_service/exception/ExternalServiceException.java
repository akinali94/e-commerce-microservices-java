package com.example.checkout_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for external service errors.
 */
public class ExternalServiceException extends ServiceException {
    public ExternalServiceException(String serviceName, String message) {
        super("Error from " + serviceName + " service: " + message, "EXTERNAL_SERVICE_ERROR", HttpStatus.SERVICE_UNAVAILABLE.value());
    }
    
    public ExternalServiceException(String serviceName, Throwable cause) {
        super("Error from " + serviceName + " service", "EXTERNAL_SERVICE_ERROR", HttpStatus.SERVICE_UNAVAILABLE.value(), cause);
    }
}