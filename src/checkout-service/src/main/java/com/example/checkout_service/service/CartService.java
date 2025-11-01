package com.example.checkout_service.service;

import com.example.checkout_service.model.CartItem;
import java.util.List;

public interface CartService {
    List<CartItem> getCart(String userId);
    void emptyCart(String userId);
}