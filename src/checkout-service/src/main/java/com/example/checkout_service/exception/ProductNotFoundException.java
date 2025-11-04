package com.example.checkout_service.exception;


import org.springframework.http.HttpStatus;

/**
 * Exception for product not found errors.
 */
public class ProductNotFoundException extends ServiceException {
    public ProductNotFoundException(String productId) {
        super("Product not found with ID: " + productId, "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }
}
