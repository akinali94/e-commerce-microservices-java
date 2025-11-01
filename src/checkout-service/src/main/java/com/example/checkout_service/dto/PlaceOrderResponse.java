package com.example.checkout_service.dto;

import com.example.checkout_service.model.*;

public class PlaceOrderResponse {
    private OrderResult order;

    public PlaceOrderResponse() {
    }

    public PlaceOrderResponse(OrderResult order) {
        this.order = order;
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