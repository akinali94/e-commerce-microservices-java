package com.example.payment_service.exception;

public class InvalidCreditCardException extends RuntimeException {
    public InvalidCreditCardException(String message){
        super(message);
    }
    
}
