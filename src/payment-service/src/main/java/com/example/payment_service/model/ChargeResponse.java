package com.example.payment_service.model;

public class ChargeResponse {
    String transactionId;

    public ChargeResponse(String id){
        this.transactionId = id;
    }
}
