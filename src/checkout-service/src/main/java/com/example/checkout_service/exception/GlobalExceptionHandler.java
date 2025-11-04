package com.example.checkout_service.exception;

import com.example.checkout_service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex, HttpServletRequest request) {
        logger.error("Service exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode(),
                ex.getErrorType(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
    }
    
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentProcessingException ex, HttpServletRequest request) {
        logger.error("Payment processing exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode(), // This will be 400 for payment errors
                ex.getErrorType(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        // Return with the proper status code (400 Bad Request)
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
    }

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (error1, error2) -> error1
                ));
        
        String errorMessage = "Validation failed: " + 
                errors.entrySet().stream()
                      .map(entry -> entry.getKey() + " " + entry.getValue())
                      .collect(Collectors.joining(", "));
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                errorMessage,
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(
            HttpClientErrorException ex, HttpServletRequest request) {
        
        logger.error("External service client error: {}", ex.getMessage());
        
        // Try to extract useful information from the response body
        String errorMessage = "Error communicating with external service";
        String errorType = "EXTERNAL_SERVICE_ERROR";
        
        if (ex.getResponseBodyAsString().contains("Credit Card is not valid")) {
            errorMessage = "The provided credit card information is invalid";
            errorType = "PAYMENT_ERROR";
        } else if (ex.getResponseBodyAsString().contains("Shipping")) {
            errorMessage = "Error processing shipping request";
            errorType = "SHIPPING_ERROR";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorType,
                errorMessage,
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Object> handleHttpServerErrorException(
            HttpServerErrorException ex, HttpServletRequest request) {
        
        logger.error("External service server error: {}", ex.getResponseBodyAsString());
        
        // Try to extract useful information from the response body
        String errorMessage = "Error from external service";
        String errorType = "EXTERNAL_SERVICE_ERROR";
        int statusCode = HttpStatus.BAD_REQUEST.value();
        
        // Extract useful information from payment service errors
        if (ex.getResponseBodyAsString().contains("Credit Card is not valid")) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "PAYMENT_ERROR");
            body.put("message", "The provided credit card information is invalid");
            body.put("path", request.getRequestURI());
            
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        // Add other error message extractions for different services
        else if (ex.getResponseBodyAsString().contains("Shipping")) {
            errorMessage = "Error processing shipping request";
            errorType = "SHIPPING_ERROR";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                statusCode,
                errorType,
                errorMessage,
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
    }
    
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(
            ResourceAccessException ex, HttpServletRequest request) {
        
        logger.error("Resource access exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "SERVICE_UNAVAILABLE",
                "The service is currently unavailable. Please try again later.",
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}