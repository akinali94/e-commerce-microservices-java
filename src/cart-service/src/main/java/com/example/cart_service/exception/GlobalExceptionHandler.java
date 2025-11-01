package com.example.cart_service.exception;

import com.example.cart_service.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the cart service.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles CartServiceException.
     *
     * @param ex The exception
     * @return An ApiResponse with the error message
     */
    @ExceptionHandler(CartServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleCartServiceException(CartServiceException ex) {
        logger.error("Cart service error", ex);
        return ApiResponse.error("Failed to process cart operation: " + ex.getMessage());
    }

    /**
     * Handles general exceptions.
     *
     * @param ex The exception
     * @return An ApiResponse with the error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneralException(Exception ex) {
        logger.error("Unexpected error", ex);
        return ApiResponse.error("An unexpected error occurred: " + ex.getMessage());
    }
}