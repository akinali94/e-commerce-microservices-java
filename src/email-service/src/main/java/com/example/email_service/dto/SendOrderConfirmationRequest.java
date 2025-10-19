package com.example.email_service.dto;

import com.example.emailservice.model.OrderResult;

public class SendOrderConfirmationRequest {
    private String email;
    private OrderResult order;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OrderResult getOrder() {
        return order;
    }

    public void setOrder(OrderResult order) {
        this.order = order;
    }
}