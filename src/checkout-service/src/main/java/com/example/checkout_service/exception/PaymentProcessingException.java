// PaymentProcessingException.java
package com.example.checkout_service.exception;

import org.springframework.http.HttpStatus;


public class PaymentProcessingException extends ServiceException {
    public PaymentProcessingException(String message) {
        super(message, "PAYMENT_ERROR", HttpStatus.BAD_REQUEST.value());
    }
    
    public PaymentProcessingException(String message, Throwable cause) {
        super(message, "PAYMENT_ERROR", HttpStatus.BAD_REQUEST.value(), cause);
    }
}