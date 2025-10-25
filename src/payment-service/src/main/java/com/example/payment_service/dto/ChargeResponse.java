package com.example.payment_service.dto;

public class ChargeResponse {
    private String transactionId;

    public ChargeResponse(String id) {
        this.transactionId = id;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}