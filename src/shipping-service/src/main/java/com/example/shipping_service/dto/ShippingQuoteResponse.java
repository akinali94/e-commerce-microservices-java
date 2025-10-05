package com.example.shipping_service.dto;

import com.example.shipping_service.model.Money;

public class ShippingQuoteResponse {
    private Money costUsd;
    
    public ShippingQuoteResponse() {}
    
    public ShippingQuoteResponse(Money costUsd) {
        this.costUsd = costUsd;
    }
    
    public Money getCostUsd() { return costUsd; }
    public void setCostUsd(Money costUsd) { this.costUsd = costUsd; }
}