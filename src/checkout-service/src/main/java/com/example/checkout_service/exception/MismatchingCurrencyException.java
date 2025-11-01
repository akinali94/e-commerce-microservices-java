package com.example.checkout_service.exception;

public class MismatchingCurrencyException extends Exception {
    public MismatchingCurrencyException(String message) {
        super(message);
    }

    public MismatchingCurrencyException(String message, Throwable cause) {
        super(message, cause);
    }
}