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

class InvalidMoneyException extends RuntimeException {
    public InvalidMoneyException(String message) {
        super(message);
    }
}

class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String message) {
        super(message);
    }
}

class OrderPlacementException extends RuntimeException {
    public OrderPlacementException(String message) {
        super(message);
    }
    
    public OrderPlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}

class EmptyCartException extends RuntimeException {
    public EmptyCartException(String userId) {
        super(String.format("Cart is empty for user: %s", userId));
    }
}

class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
    
    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ShippingFailedException extends RuntimeException {
    public ShippingFailedException(String message) {
        super(message);
    }
    
    public ShippingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}