package com.example.cart_service.model;

import java.io.Serializable;

/**
 * Represents an item in a shopping cart.
 */
public class CartItem implements Serializable {
    
    private String productId;
    private int quantity;

    public CartItem() {
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param productId The unique identifier for the product
     * @param quantity The quantity of the product in the cart
     */
    public CartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    /**
     * Gets the product ID.
     * 
     * @return The product ID
     */
    public String getProductId() {
        return productId;
    }
    
    /**
     * Sets the product ID.
     * 
     * @param productId The product ID to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    /**
     * Gets the quantity.
     * 
     * @return The quantity
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * Sets the quantity.
     * 
     * @param quantity The quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CartItem cartItem = (CartItem) o;
        
        if (quantity != cartItem.quantity) return false;
        return productId != null ? productId.equals(cartItem.productId) : cartItem.productId == null;
    }
    
    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + quantity;
        return result;
    }
}