package com.example.cart_service.repository;

import com.example.cart_service.model.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Redis implementation of CartRepository.
 */
@Repository
@Profile("redis")
public class RedisCartRepository implements CartRepository {

    private static final Logger logger = LoggerFactory.getLogger(RedisCartRepository.class);
    private static final String CART_KEY_PREFIX = "cart:";
    private static final long CART_EXPIRY = 30; // 30 days

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Constructor.
     *
     * @param redisTemplate The Redis template
     */
    public RedisCartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Builds the Redis key for a cart.
     *
     * @param userId The user ID
     * @return The Redis key
     */
    private String buildCartKey(String userId) {
        return CART_KEY_PREFIX + userId;
    }

    @Override
    public CompletableFuture<Cart> getCart(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Getting cart for user: {}", userId);
                
                String cartKey = buildCartKey(userId);
                Object result = redisTemplate.opsForValue().get(cartKey);
                
                // Return an empty cart if none exists
                if (result == null) {
                    logger.debug("No cart found for user: {}, creating new empty cart", userId);
                    return new Cart(userId);
                }
                
                // Handle potential class cast exceptions
                if (result instanceof Cart) {
                    logger.debug("Cart found for user: {}", userId);
                    Cart cart = (Cart) result;
                    
                    // Ensure the cart has the correct user ID
                    if (cart.getUserId() == null || !cart.getUserId().equals(userId)) {
                        cart.setUserId(userId);
                    }
                    
                    return cart;
                } else {
                    logger.warn("Invalid cart data found for user: {}, type: {}", userId, result.getClass().getName());
                    // If the data is corrupted, return a new empty cart
                    return new Cart(userId);
                }
                
            } catch (Exception e) {
                logger.error("Failed to get cart for user: {}", userId, e);
                // Instead of throwing, return an empty cart on error
                return new Cart(userId);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> saveCart(Cart cart) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Saving cart for user: {}", cart.getUserId());
                
                String cartKey = buildCartKey(cart.getUserId());
                redisTemplate.opsForValue().set(cartKey, cart);
                redisTemplate.expire(cartKey, CART_EXPIRY, TimeUnit.DAYS);
                
                logger.debug("Cart saved successfully for user: {}", cart.getUserId());
                return true;
                
            } catch (Exception e) {
                logger.error("Failed to save cart for user: {}", cart.getUserId(), e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteCart(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Deleting cart for user: {}", userId);
                
                String cartKey = buildCartKey(userId);
                Boolean deleted = redisTemplate.delete(cartKey);
                
                logger.debug("Cart deleted successfully for user: {}", userId);
                return deleted != null && deleted;
                
            } catch (Exception e) {
                logger.error("Failed to delete cart for user: {}", userId, e);
                return false;
            }
        });
    }
}