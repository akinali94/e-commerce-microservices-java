package com.example.checkout_service.exception;

class ShippingFailedException extends RuntimeException {
    public ShippingFailedException(String message) {
        super(message);
    }
    
    public ShippingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
