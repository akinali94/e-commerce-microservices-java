package com.example.cart_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user's shopping cart.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart implements Serializable {
    
    private String userId;
    private List<CartItem> items;
    
    /**
     * Default constructor for serialization.
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
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }
    
    /**
     * Sets the cart items.
     * 
     * @param items The list of cart items to set
     */
    public void setItems(List<CartItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
    
    /**
     * Adds an item to the cart.
     * If the item with the same productId already exists, increases its quantity.
     * 
     * @param productId The product ID
     * @param quantity The quantity to add
     */
    public void addItem(String productId, int quantity) {
        if (productId == null || quantity <= 0) {
            return;
        }
        
        CartItem existingItem = findItemByProductId(productId);
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            getItems().add(new CartItem(productId, quantity));
        }
    }
    
    /**
     * Finds an item in the cart by its product ID.
     * 
     * @param productId The product ID to search for
     * @return The cart item if found, or null otherwise
     */
    public CartItem findItemByProductId(String productId) {
        if (productId == null || getItems().isEmpty()) {
            return null;
        }
        
        return getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Removes an item from the cart by product ID.
     * 
     * @param productId The product ID to remove
     * @return true if the item was removed, false otherwise
     */
    public boolean removeItem(String productId) {
        if (productId == null || getItems().isEmpty()) {
            return false;
        }
        
        return getItems().removeIf(item -> productId.equals(item.getProductId()));
    }
    
    /**
     * Checks if the cart is empty.
     * 
     * @return true if the cart has no items, false otherwise
     */
    public boolean isEmpty() {
        return getItems().isEmpty();
    }
    
    /**
     * Clears all items from the cart.
     */
    public void clear() {
        getItems().clear();
    }
    
    /**
     * Gets the total number of items in the cart.
     * 
     * @return The total quantity of all items
     */
    public int getTotalQuantity() {
        return getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Cart cart = (Cart) o;
        
        if (!Objects.equals(userId, cart.userId)) return false;
        return Objects.equals(getItems(), cart.getItems());
    }
    
    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "Cart{" +
                "userId='" + userId + '\'' +
                ", items=" + getItems() +
                ", isEmpty=" + isEmpty() +
                ", totalQuantity=" + getTotalQuantity() +
                '}';
    }
}