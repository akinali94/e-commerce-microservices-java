package com.example.shipping_service.dto;

import com.example.shipping_service.model.Address;
import com.example.shipping_service.model.CartItem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ShipOrderRequest {
    @NotNull(message = "Address is required")
    @Valid
    private Address address;
    
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<CartItem> items;
    
    public ShipOrderRequest() {}
    
    public ShipOrderRequest(Address address, List<CartItem> items) {
        this.address = address;
        this.items = items;
    }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}