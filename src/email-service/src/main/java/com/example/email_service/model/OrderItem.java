package com.example.email_service.model;

public class OrderItem {
    private CartItem item;
    private Money cost;

    public CartItem getItem() {
        return item;
    }

    public void setItem(CartItem item) {
        this.item = item;
    }

    public Money getCost() {
        return cost;
    }

    public void setCost(Money cost) {
        this.cost = cost;
    }
}
