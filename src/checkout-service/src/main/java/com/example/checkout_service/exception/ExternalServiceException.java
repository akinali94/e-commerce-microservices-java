package com.example.checkout_service.exception;


/**
 * Exception thrown when external service communication fails.
 */
public class ExternalServiceException extends RuntimeException {
    private final String serviceName;
    
    public ExternalServiceException(String serviceName, String message) {
        super(String.format("Error communicating with %s: %s", serviceName, message));
        this.serviceName = serviceName;
    }
    
    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(String.format("Error communicating with %s: %s", serviceName, message), cause);
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
}