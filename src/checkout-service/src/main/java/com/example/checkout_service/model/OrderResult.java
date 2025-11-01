package com.example.checkout_service.model;

import java.util.List;

public class OrderResult {
    private String orderId;
    private String shippingTrackingId;
    private Money shippingCost;
    private Address shippingAddress;
    private List<OrderItem> items;

    public OrderResult() {
    }

    public OrderResult(String orderId, String shippingTrackingId, Money shippingCost, 
                      Address shippingAddress, List<OrderItem> items) {
        this.orderId = orderId;
        this.shippingTrackingId = shippingTrackingId;
        this.shippingCost = shippingCost;
        this.shippingAddress = shippingAddress;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShippingTrackingId() {
        return shippingTrackingId;
    }

    public void setShippingTrackingId(String shippingTrackingId) {
        this.shippingTrackingId = shippingTrackingId;
    }

    public Money getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Money shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderResult{" +
                "orderId='" + orderId + '\'' +
                ", shippingTrackingId='" + shippingTrackingId + '\'' +
                ", shippingCost=" + shippingCost +
                ", shippingAddress=" + shippingAddress +
                ", items=" + items +
                '}';
    }
}