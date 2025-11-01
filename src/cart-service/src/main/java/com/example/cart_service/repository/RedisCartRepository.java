package com.example.cart_service.repository;

import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Redis implementation of the CartRepository interface.
 */
@Repository
public class RedisCartRepository implements CartRepository {

    private static final Logger logger = LoggerFactory.getLogger(RedisCartRepository.class);
    private static final String CART_KEY_PREFIX = "cart:";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Constructor with RedisTemplate dependency.
     *
     * @param redisTemplate The Redis template
     */
    public RedisCartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Builds a Redis key for a user's cart.
     *
     * @param userId The user ID
     * @return The Redis key
     */
    private String buildCartKey(String userId) {
        return CART_KEY_PREFIX + userId;
    }

    @Override
    public CompletableFuture<Void> addItem(String userId, String productId, int quantity) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Adding item to cart for user: {}, productId: {}, quantity: {}", userId, productId, quantity);
                
                String cartKey = buildCartKey(userId);
                Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
                
                if (cart == null) {
                    // Create a new cart if one doesn't exist
                    cart = new Cart();
                    cart.setUserId(userId);
                    cart.setItems(new ArrayList<>());
                }
                
                CartItem existingItem = cart.findItemByProductId(productId);
                
                if (existingItem != null) {
                    // Increase quantity if the item already exists
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                } else {
                    // Add a new item to the cart
                    cart.getItems().add(new CartItem(productId, quantity));
                }
                
                // Save the updated cart to Redis
                redisTemplate.opsForValue().set(cartKey, cart);
                
            } catch (Exception e) {
                logger.error("Failed to add item to cart", e);
                throw new RuntimeException("Failed to add item to cart: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Cart> getCart(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Getting cart for user: {}", userId);
                
                String cartKey = buildCartKey(userId);
                Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
                
                // Return an empty cart if none exists
                if (cart == null) {
                    return new Cart(userId, new ArrayList<>());
                }
                
                return cart;
                
            } catch (Exception e) {
                logger.error("Failed to get cart", e);
                throw new RuntimeException("Failed to get cart: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> emptyCart(String userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Emptying cart for user: {}", userId);
                
                String cartKey = buildCartKey(userId);
                
                // Create a new empty cart
                Cart emptyCart = new Cart();
                emptyCart.setUserId(userId);
                emptyCart.setItems(new ArrayList<>());
                
                // Save the empty cart to Redis
                redisTemplate.opsForValue().set(cartKey, emptyCart);
                
            } catch (Exception e) {
                logger.error("Failed to empty cart", e);
                throw new RuntimeException("Failed to empty cart: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public boolean isHealthy() {
        try {
            // Check Redis connection
            return Boolean.TRUE.equals(redisTemplate.getConnectionFactory().getConnection().ping());
        } catch (Exception e) {
            logger.error("Redis health check failed", e);
            return false;
        }
    }
}