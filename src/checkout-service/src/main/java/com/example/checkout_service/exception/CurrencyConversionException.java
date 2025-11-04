package com.example.checkout_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for currency-related errors.
 */
public class CurrencyConversionException extends ServiceException {
    public CurrencyConversionException(String message) {
        super(message, "CURRENCY_ERROR", HttpStatus.BAD_REQUEST.value());
    }
    
    public CurrencyConversionException(String message, Throwable cause) {
        super(message, "CURRENCY_ERROR", HttpStatus.BAD_REQUEST.value(), cause);
    }
}
