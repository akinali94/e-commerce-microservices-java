package com.example.payment_service.model;

public class ChargeRequest {
    private Amount amount;
    private CreditCard creditCard;

    public Amount getAmount(){
        return this.amount;
    }

    public CreditCard getCreditCard(){
        return this.creditCard;
    }
}
