package com.example.payment_service.exception;

public class UnacceptedCreditCardException extends RuntimeException{
    public UnacceptedCreditCardException(String message){
        super(message);
    }
}
