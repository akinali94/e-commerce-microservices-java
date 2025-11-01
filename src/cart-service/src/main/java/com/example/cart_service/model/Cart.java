package com.example.cart_service.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's shopping cart.
 */
public class Cart implements Serializable {
    
    private String userId;
    private List<CartItem> items;
    
    /**
     * Default constructor.
     */
    public Cart() {
        this.items = new ArrayList<>();
    }
    
    /**
     * Constructor with userId.
     * 
     * @param userId The unique identifier for the user
     */
    public Cart(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param userId The unique identifier for the user
     * @param items The list of cart items
     */
    public Cart(String userId, List<CartItem> items) {
        this.userId = userId;
        this.items = items != null ? items : new ArrayList<>();
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
     * Gets the cart items.
     * 
     * @return The list of cart items
     */
    public List<CartItem> getItems() {
        return items;
    }
    
    /**
     * Sets the cart items.
     * 
     * @param items The list of cart items to set
     */
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    /**
     * Adds an item to the cart.
     * If the item with the same productId already exists, increases its quantity.
     * 
     * @param productId The product ID
     * @param quantity The quantity to add
     */
    public void addItem(String productId, int quantity) {
        CartItem existingItem = findItemByProductId(productId);
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            items.add(new CartItem(productId, quantity));
        }
    }
    
    /**
     * Finds an item in the cart by its product ID.
     * 
     * @param productId The product ID to search for
     * @return The cart item if found, or null otherwise
     */
    public CartItem findItemByProductId(String productId) {
        if (productId == null || items == null) {
            return null;
        }
        
        return items.stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Checks if the cart is empty.
     * 
     * @return true if the cart has no items, false otherwise
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Cart{" +
                "userId='" + userId + '\'' +
                ", items=" + items +
                '}';
    }
}