package com.example.checkout_service.dto;

import com.example.checkout_service.model.*;

public class PlaceOrderResponse {

    private String message;
    private OrderResult order;

    public PlaceOrderResponse() {
    }

    public PlaceOrderResponse(String message, OrderResult order) {
        this.message = message;
        this.order = order;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderResult getOrder() {
        return order;
    }

    public void setOrder(OrderResult order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "PlaceOrderResponse{" +
                "order=" + order +
                '}';
    }
}