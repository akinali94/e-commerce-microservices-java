package com.example.checkout_service.exception;

class OrderPlacementException extends RuntimeException {
    public OrderPlacementException(String message) {
        super(message);
    }
    
    public OrderPlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}

