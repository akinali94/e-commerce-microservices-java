package com.example.recommendation_service.exception;

/**
 * Exception thrown when there is an error communicating with the product catalog service.
 */
public class ProductCatalogServiceException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ProductCatalogServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ProductCatalogServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}