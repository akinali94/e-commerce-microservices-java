package com.example.checkout_service.service;

import com.example.checkout_service.dto.PlaceOrderRequest;
import com.example.checkout_service.dto.PlaceOrderResponse;

public interface CheckoutService {
    PlaceOrderResponse placeOrder(PlaceOrderRequest request);
}