package com.example.checkout_service.service;

import com.example.checkout_service.model.Address;
import com.example.checkout_service.model.CartItem;
import com.example.checkout_service.model.Money;

import java.util.List;

public interface ShippingService {
    Money getShippingQuote(Address address, List<CartItem> items);
    String shipOrder(Address address, List<CartItem> items);
}