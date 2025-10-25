package com.example.payment_service.dto;

import com.example.payment_service.model.Amount;
import com.example.payment_service.model.CreditCard;

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
