package com.example.shipping_service.dto;

import com.example.shipping_service.model.Address;
import com.example.shipping_service.model.CartItem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ShippingQuoteRequest {

    private Address address;
    
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<CartItem> items;
    
    public ShippingQuoteRequest() {}
    
    public ShippingQuoteRequest(Address address, List<CartItem> items) {
        this.address = address;
        this.items = items;
    }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}