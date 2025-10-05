package com.example.shipping_service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public class CartItem {
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    public CartItem() {}
    
    public CartItem(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}