package com.example.cart_service.controller;

import com.example.cart_service.dto.AddItemRequest;
import com.example.cart_service.dto.ApiResponse;
import com.example.cart_service.model.Cart;
import com.example.cart_service.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST controller for cart operations.
 */
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    /**
     * Constructor with CartService dependency.
     *
     * @param cartService The cart service
     */
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Adds an item to a cart.
     *
     * @param request The add item request
     * @return A CompletableFuture containing the API response
     */
    @PostMapping("/items")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> addItem(@RequestBody @Validated AddItemRequest request) {
        logger.info("Adding item to cart for user: {}", request.getUserId());

        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("User ID is required"))
            );
        }

        if (request.getItem() == null) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("Item is required"))
            );
        }

        if (request.getItem().getProductId() == null || request.getItem().getProductId().trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("Product ID is required"))
            );
        }

        if (request.getItem().getQuantity() <= 0) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("Quantity must be greater than 0"))
            );
        }

        return cartService
                .addItem(request.getUserId(), request.getItem().getProductId(), request.getItem().getQuantity())
                .thenApply(result -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiResponse.<Void>success("Item added to cart successfully"))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to add item to cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Void>error("Failed to add item to cart: " + ex.getMessage()));
                });
    }

    /**
     * Gets a cart by user ID.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the cart
     */
    @GetMapping("/{userId}")
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
     * Empties a cart by user ID.
     *
     * @param userId The user ID
     * @return A CompletableFuture containing the API response
     */
    @DeleteMapping("/{userId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> emptyCart(@PathVariable String userId) {
        logger.info("Emptying cart for user: {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("User ID is required"))
            );
        }

        return cartService
                .emptyCart(userId)
                .thenApply(result -> ResponseEntity
                        .ok()
                        .body(ApiResponse.<Void>success("Cart emptied successfully"))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to empty cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Void>error("Failed to empty cart: " + ex.getMessage()));
                });
    }

    /**
     * Alternative endpoint to add item to cart with path variables.
     * Useful for simple additions without requiring a full JSON body.
     *
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The quantity to add
     * @return A CompletableFuture containing the API response
     */
    @PostMapping("/{userId}/items/{productId}/quantity/{quantity}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> addItemAlternative(
            @PathVariable String userId,
            @PathVariable String productId,
            @PathVariable int quantity) {
        
        logger.info("Adding item to cart for user: {}, product: {}, quantity: {}", userId, productId, quantity);

        if (userId == null || userId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("User ID is required"))
            );
        }

        if (productId == null || productId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("Product ID is required"))
            );
        }

        if (quantity <= 0) {
            return CompletableFuture.completedFuture(
                    ResponseEntity
                            .badRequest()
                            .body(ApiResponse.<Void>error("Quantity must be greater than 0"))
            );
        }

        return cartService
                .addItem(userId, productId, quantity)
                .thenApply(result -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiResponse.<Void>success("Item added to cart successfully"))
                )
                .exceptionally(ex -> {
                    logger.error("Failed to add item to cart", ex);
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.<Void>error("Failed to add item to cart: " + ex.getMessage()));
                });
    }

    /**
     * Health check endpoint.
     *
     * @return The health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        boolean isHealthy = cartService.isHealthy();
        
        if (isHealthy) {
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.<String>success("Cart service is healthy", "UP"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.<String>error("Cart service is unhealthy"));
        }
    }
}