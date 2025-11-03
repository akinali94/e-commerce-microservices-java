package com.example.cart_service.service;

import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface CartService {

    /**
     * Adds an item to the cart.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The quantity to add
     * @return A CompletableFuture representing the asynchronous operation
     */
    CompletableFuture<Cart> addItemToCart(String userId, String productId, int quantity);

    CompletableFuture<Cart> updateCartItem(String userId, String productId, int quantity);

    /**
     * Retrieves a user's cart.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the cart
     */
    CompletableFuture<Cart> getCart(String userId);

    /**
     * Empties a user's cart.
     *
     * @param userId The user ID
     * @return A CompletableFuture representing the asynchronous operation
     */
    CompletableFuture<Cart> removeItemFromCart(String userId, String productId);

    CompletableFuture<Cart> setCartItems(String userId, List<CartItem> items);

    CompletableFuture<Cart> clearCart(String userId);

    CompletableFuture<Boolean> deleteCart(String userId);

    /**
     * Checks the health of the cart service.
     *
     * @return true if the service is healthy, false otherwise
     */
    //boolean isHealthy();
}