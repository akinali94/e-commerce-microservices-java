package com.example.cart_service.exception;

/**
 * General exception for cart service operations.
 */
public class CartServiceException extends RuntimeException {


    public CartServiceException() {
        super();
    }

    public CartServiceException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message The error message
     * @param cause The cause of the exception
     */
    public CartServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     *
     * @param cause The cause of the exception
     */
    public CartServiceException(Throwable cause) {
        super(cause);
    }
}