package com.example.cart_service.dto;

/**
 * DTO for emptying a cart.
 */
public class EmptyCartRequest {
    
    private String userId;
    

    public EmptyCartRequest() {
    }
    

    public EmptyCartRequest(String userId) {
        this.userId = userId;
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
}