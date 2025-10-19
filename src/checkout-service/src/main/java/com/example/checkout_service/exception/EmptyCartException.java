package com.example.checkout_service.exception;

class EmptyCartException extends RuntimeException {
    public EmptyCartException(String userId) {
        super(String.format("Cart is empty for user: %s", userId));
    }
}