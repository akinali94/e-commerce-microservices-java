package com.example.payment_service.exception;

public class ExpiredCreditCardException extends RuntimeException {
    public ExpiredCreditCardException(String message){
        super(message);
    }
}