package com.example.cart_service.service;

import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for cart operations.
 */
public interface CartService {
    
    /**
     * Gets a cart by user ID.
     * If no cart exists, an empty cart is returned.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the cart
     */
    CompletableFuture<Cart> getCart(String userId);
    
    /**
     * Adds an item to a cart.
     * If the item already exists, its quantity is increased.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The quantity to add
     * @return A CompletableFuture containing the updated cart
     */
    CompletableFuture<Cart> addItemToCart(String userId, String productId, int quantity);
    
    /**
     * Updates the quantity of an item in a cart.
     * If the quantity is 0, the item is removed.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The new quantity
     * @return A CompletableFuture containing the updated cart
     */
    CompletableFuture<Cart> updateCartItem(String userId, String productId, int quantity);
    
    /**
     * Removes an item from a cart.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @return A CompletableFuture containing the updated cart
     */
    CompletableFuture<Cart> removeItemFromCart(String userId, String productId);
    
    /**
     * Sets all items in a cart.
     * This replaces any existing items.
     *
     * @param userId The user ID
     * @param items The list of items to set
     * @return A CompletableFuture containing the updated cart
     */
    CompletableFuture<Cart> setCartItems(String userId, List<CartItem> items);
    
    /**
     * Clears a cart.
     * This removes all items from the cart.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the cleared cart
     */
    CompletableFuture<Cart> clearCart(String userId);
    
    /**
     * Deletes a cart.
     * This removes the entire cart, not just its contents.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing true if the cart was deleted, false otherwise
     */
    CompletableFuture<Boolean> deleteCart(String userId);
}