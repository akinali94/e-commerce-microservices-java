package com.example.cart_service.service;

import com.example.cart_service.exception.CartServiceException;
import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import com.example.cart_service.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of CartService.
 */
@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private final CartRepository cartRepository;

    /**
     * Constructor.
     *
     * @param cartRepository The cart repository
     */
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public CompletableFuture<Cart> getCart(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        logger.debug("Getting cart for user: {}", userId);
        // The repository now handles the empty cart case and exceptions
        return cartRepository.getCart(userId);
    }

    @Override
    public CompletableFuture<Cart> addItemToCart(String userId, String productId, int quantity) {
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("Product ID is required"));
        }
        
        if (quantity <= 0) {
            return CompletableFuture.failedFuture(new CartServiceException("Quantity must be positive"));
        }
        
        logger.debug("Adding item to cart for user: {}, productId: {}, quantity: {}", userId, productId, quantity);
        
        return cartRepository.getCart(userId)
                .thenCompose(cart -> {
                    // Add the item to the cart
                    cart.addItem(productId, quantity);
                    
                    // Save the updated cart
                    return cartRepository.saveCart(cart)
                            .thenApply(saved -> {
                                if (!saved) {
                                    throw new CartServiceException("Failed to save cart");
                                }
                                return cart;
                            });
                });
    }

    @Override
    public CompletableFuture<Cart> updateCartItem(String userId, String productId, int quantity) {
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("Product ID is required"));
        }
        
        if (quantity < 0) {
            return CompletableFuture.failedFuture(new CartServiceException("Quantity must be non-negative"));
        }
        
        logger.debug("Updating cart item for user: {}, productId: {}, quantity: {}", userId, productId, quantity);
        
        return cartRepository.getCart(userId)
                .thenCompose(cart -> {
                    // Find the item in the cart
                    CartItem existingItem = cart.findItemByProductId(productId);
                    
                    // If item not found, return an error
                    if (existingItem == null) {
                        return CompletableFuture.failedFuture(
                                new CartServiceException("Item not found in cart: " + productId));
                    }
                    
                    if (quantity == 0) {
                        // Remove the item if quantity is 0
                        cart.removeItem(productId);
                    } else {
                        // Update the quantity
                        existingItem.setQuantity(quantity);
                    }
                    
                    // Save the updated cart
                    return cartRepository.saveCart(cart)
                            .thenApply(saved -> {
                                if (!saved) {
                                    throw new CartServiceException("Failed to save cart");
                                }
                                return cart;
                            });
                });
    }

    @Override
    public CompletableFuture<Cart> removeItemFromCart(String userId, String productId) {
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("Product ID is required"));
        }
        
        logger.debug("Removing item from cart for user: {}, productId: {}", userId, productId);
        
        return cartRepository.getCart(userId)
                .thenCompose(cart -> {
                    // Remove the item from the cart
                    boolean removed = cart.removeItem(productId);
                    
                    // If item not found, return the cart unchanged
                    if (!removed) {
                        logger.debug("Item not found in cart: {}, productId: {}", userId, productId);
                    }
                    
                    // Save the updated cart
                    return cartRepository.saveCart(cart)
                            .thenApply(saved -> {
                                if (!saved) {
                                    throw new CartServiceException("Failed to save cart");
                                }
                                return cart;
                            });
                });
    }

    @Override
    public CompletableFuture<Cart> setCartItems(String userId, List<CartItem> items) {
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        logger.debug("Setting cart items for user: {}", userId);
        
        return cartRepository.getCart(userId)
                .thenCompose(cart -> {
                    // Set the items in the cart
                    cart.setItems(items);
                    
                    // Save the updated cart
                    return cartRepository.saveCart(cart)
                            .thenApply(saved -> {
                                if (!saved) {
                                    throw new CartServiceException("Failed to save cart");
                                }
                                return cart;
                            });
                });
    }

    @Override
    public CompletableFuture<Cart> clearCart(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        logger.debug("Clearing cart for user: {}", userId);
        
        return cartRepository.getCart(userId)
                .thenCompose(cart -> {
                    // Clear the cart
                    cart.clear();
                    
                    // Save the updated cart
                    return cartRepository.saveCart(cart)
                            .thenApply(saved -> {
                                if (!saved) {
                                    throw new CartServiceException("Failed to save cart");
                                }
                                return cart;
                            });
                });
    }

    @Override
    public CompletableFuture<Boolean> deleteCart(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.failedFuture(new CartServiceException("User ID is required"));
        }
        
        logger.debug("Deleting cart for user: {}", userId);
        
        return cartRepository.deleteCart(userId);
    }
}