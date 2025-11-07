package com.example.checkout_service.dto;

import com.example.checkout_service.model.Money;

public class ShippingQuoteResponse {
    private Money costUsd;
        
    public Money getCostUsd() {
        return costUsd;
    }
        
    public void setCostUsd(Money costUsd) {
        this.costUsd = costUsd;
    }  
}
