package com.example.cart_service.repository;

import com.example.cart_service.model.Cart;

import java.util.concurrent.CompletableFuture;


public interface CartRepository {

    /**
     * Adds an item to the cart.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The quantity to add
     * @return A CompletableFuture representing the asynchronous operation
     */
    CompletableFuture<Void> addItem(String userId, String productId, int quantity);

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
    CompletableFuture<Void> emptyCart(String userId);

    /**
     * Checks the health of the repository.
     *
     * @return true if the repository is healthy, false otherwise
     */
    boolean isHealthy();
}