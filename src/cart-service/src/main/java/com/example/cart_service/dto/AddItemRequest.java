package com.example.cart_service.dto;

import com.example.cart_service.model.CartItem;

/**
 * DTO for adding an item to the cart.
 */
public class AddItemRequest {
    
    private String userId;
    private CartItem item;
    

    public AddItemRequest() {
    }
    
    public AddItemRequest(String userId, CartItem item) {
        this.userId = userId;
        this.item = item;
    }
    
    /**
     * Gets the user ID.
     * 
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Sets the user ID.
     * 
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the cart item.
     * 
     * @return The cart item
     */
    public CartItem getItem() {
        return item;
    }
    
    /**
     * Sets the cart item.
     * 
     * @param item The cart item to set
     */
    public void setItem(CartItem item) {
        this.item = item;
    }
}