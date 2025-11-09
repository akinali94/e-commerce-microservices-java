package com.example.cart_service.controller;

import com.example.cart_service.dto.ApiResponse;
import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import com.example.cart_service.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    private final CartService cartService;


    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
    * GET /api
    * API information endpoint
    */
    @GetMapping("/cartservice")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Cart Service API");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/v1/cartservice", "API information");
        endpoints.put("GET /api/v1/health", "Health check");
        endpoints.put("GET /api/v1/carts/{userId}", "Get cart by user ID");
        endpoints.put("POST /api/v1/carts/{userId}/items", "Add item to cart");
        endpoints.put("POST /api/v1/carts/{userId}/items/{productId}", "Updates an item in a cart");
        endpoints.put("DELETE /api/v1/carts/{userId}", "Empty cart");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }

    /**
     * Gets a cart by user ID.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the cart
     */
    @GetMapping("/carts/{userId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Cart>>> getCart(@PathVariable String userId) {
        logger.info("Getting cart for user: {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("User ID is required"))
            );
        }

        return cartService
                .getCart(userId)
                .thenApply(cart -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Cart>success("Cart retrieved successfully", cart))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to get cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Cart>error("Failed to get cart: " + ex.getMessage()));
                });
    }

    /**
     * Adds an item to a cart.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The quantity
     * @return A CompletableFuture containing the updated cart
     */
    @PostMapping("/carts/{userId}/items")
    public CompletableFuture<ResponseEntity<ApiResponse<Cart>>> addItemToCart(
            @PathVariable String userId,
            @RequestParam String productId,
            @RequestParam int quantity) {
        
        logger.info("Adding item to cart. userId: {}, productId: {}, quantity: {}", userId, productId, quantity);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("User ID is required"))
            );
        }

        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("Product ID is required"))
            );
        }

        if (quantity <= 0) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("Quantity must be positive"))
            );
        }

        return cartService
                .addItemToCart(userId, productId, quantity)
                .thenApply(cart -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Cart>success("Item added to cart", cart))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to add item to cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Cart>error("Failed to add item to cart: " + ex.getMessage()));
                });
    }

    /**
     * Updates an item in a cart.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The new quantity
     * @return A CompletableFuture containing the updated cart
     */
    @PutMapping("/carts/{userId}/items/{productId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Cart>>> updateCartItem(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestParam int quantity) {
        
        logger.info("Updating cart item. userId: {}, productId: {}, quantity: {}", userId, productId, quantity);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("User ID is required"))
            );
        }

        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("Product ID is required"))
            );
        }

        if (quantity < 0) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("Quantity must be non-negative"))
            );
        }

        return cartService
                .updateCartItem(userId, productId, quantity)
                .thenApply(cart -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Cart>success("Cart item updated", cart))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to update cart item", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Cart>error("Failed to update cart item: " + ex.getMessage()));
                });
    }

    /**
     * Removes an item from a cart.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @return A CompletableFuture containing the updated cart
     */
    @DeleteMapping("/carts/{userId}/items/{productId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Cart>>> removeItemFromCart(
            @PathVariable String userId,
            @PathVariable String productId) {
        
        logger.info("Removing item from cart. userId: {}, productId: {}", userId, productId);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("User ID is required"))
            );
        }

        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("Product ID is required"))
            );
        }

        return cartService
                .removeItemFromCart(userId, productId)
                .thenApply(cart -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Cart>success("Item removed from cart", cart))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to remove item from cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Cart>error("Failed to remove item from cart: " + ex.getMessage()));
                });
    }

    /**
     * Sets all items in a cart.
     *
     * @param userId The user ID
     * @param items The list of items to set
     * @return A CompletableFuture containing the updated cart
     */
    @PutMapping("/carts/{userId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Cart>>> setCartItems(
            @PathVariable String userId,
            @RequestBody List<CartItem> items) {
        
        logger.info("Setting cart items for user: {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("User ID is required"))
            );
        }

        return cartService
                .setCartItems(userId, items)
                .thenApply(cart -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Cart>success("Cart items updated", cart))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to set cart items", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Cart>error("Failed to set cart items: " + ex.getMessage()));
                });
    }

    /**
     * Clears a cart.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the cleared cart
     */
    @DeleteMapping("/carts/{userId}/items")
    public CompletableFuture<ResponseEntity<ApiResponse<Cart>>> clearCart(@PathVariable String userId) {
        logger.info("Clearing cart for user: {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Cart>error("User ID is required"))
            );
        }

        return cartService
                .clearCart(userId)
                .thenApply(cart -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Cart>success("Cart cleared", cart))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to clear cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Cart>error("Failed to clear cart: " + ex.getMessage()));
                });
    }

    /**
     * Deletes a cart.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the result of the operation
     */
    @DeleteMapping("/carts/{userId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Boolean>>> deleteCart(@PathVariable String userId) {
        logger.info("Deleting cart for user: {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Boolean>error("User ID is required"))
            );
        }

        return cartService
                .deleteCart(userId)
                .thenApply(result -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Boolean>success("Cart deleted", result))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to delete cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Boolean>error("Failed to delete cart: " + ex.getMessage()));
                });
    }

    @GetMapping("/health")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "cart-service");
        health.put("timestamp", LocalDateTime.now());
        
        return CompletableFuture.completedFuture(ResponseEntity.ok(health));
    }
}