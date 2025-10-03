package com.example.payment_service.model;

public class CardDetails {
    
    String cardType;
    boolean valid;
    boolean isNotExpired;

    public CardDetails(String cardType, boolean isValid, boolean isExpired){
        this.cardType = cardType;
        this.valid = isValid;
        this.isNotExpired = isExpired;
    }

    public String getCardType(){
        return this.cardType;
    }

    public boolean getValid(){
        return this.valid;
    }

    public boolean getIsNotExpired(){
        return this.isNotExpired;
    }
}
