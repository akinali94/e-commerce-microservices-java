package com.example.currency_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.currency_service.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Unsupported currency exception handler
     */
    @ExceptionHandler(UnsupportedCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedCurrency(UnsupportedCurrencyException ex) {
        logger.error("Unsupported currency error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Illegal argument exception handler
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Illegal argument error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Type mismatch exception handler (örn: amount string ise)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Type mismatch error: {}", ex.getMessage());

        String message = String.format("Invalid value for parameter '%s'. Expected type: %s",
                ex.getName(), ex.getRequiredType().getSimpleName());

        ErrorResponse error = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Missing parameter exception handler
     */
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            org.springframework.web.bind.MissingServletRequestParameterException ex) {
        logger.error("Missing parameter error: {}", ex.getMessage());

        String message = String.format("Missing required parameter: %s", ex.getParameterName());

        ErrorResponse error = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Generic exception handler (catch-all)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled error: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
