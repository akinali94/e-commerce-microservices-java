package com.example.cart_service.service;

import com.example.cart_service.exception.CartServiceException;
import com.example.cart_service.model.Cart;
import com.example.cart_service.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the CartService interface.
 */
@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;

    /**
     * Constructor with CartRepository dependency.
     *
     * @param cartRepository The cart repository
     */
    @Autowired
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public CompletableFuture<Void> addItem(String userId, String productId, int quantity) {
        logger.debug("Adding item to cart: userId={}, productId={}, quantity={}", userId, productId, quantity);
        
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("Product ID is required"));
        }
        
        if (quantity <= 0) {
            return CompletableFuture.failedFuture(new CartServiceException("Quantity must be greater than 0"));
        }
        
        return cartRepository.addItem(userId, productId, quantity)
                .exceptionally(ex -> {
                    logger.error("Failed to add item to cart", ex);
                    throw new CartServiceException("Failed to add item to cart", ex);
                });
    }

    @Override
    public CompletableFuture<Cart> getCart(String userId) {
        logger.debug("Getting cart for user: {}", userId);
        
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        return cartRepository.getCart(userId)
                .exceptionally(ex -> {
                    logger.error("Failed to get cart", ex);
                    throw new CartServiceException("Failed to get cart", ex);
                });
    }

    @Override
    public CompletableFuture<Void> emptyCart(String userId) {
        logger.debug("Emptying cart for user: {}", userId);
        
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        return cartRepository.emptyCart(userId)
                .exceptionally(ex -> {
                    logger.error("Failed to empty cart", ex);
                    throw new CartServiceException("Failed to empty cart", ex);
                });
    }

    @Override
    public boolean isHealthy() {
        return cartRepository.isHealthy();
    }
}