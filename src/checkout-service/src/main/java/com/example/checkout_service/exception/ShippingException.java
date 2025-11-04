package com.example.checkout_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for shipping errors.
 */
public class ShippingException extends ServiceException {
    public ShippingException(String message) {
        super(message, "SHIPPING_ERROR", HttpStatus.BAD_REQUEST.value());
    }
    
    public ShippingException(String message, Throwable cause) {
        super(message, "SHIPPING_ERROR", HttpStatus.BAD_REQUEST.value(), cause);
    }
}

