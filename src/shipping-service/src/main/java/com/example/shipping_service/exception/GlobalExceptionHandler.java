package com.example.shipping_service.exception;

import com.example.shipping_service.dto.ErrorResponse;
import com.example.shipping_service.util.LoggerUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final LoggerUtil logger;
    
    public GlobalExceptionHandler(LoggerUtil logger) {
        this.logger = logger;
    }
    
    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        Map<String, Object> logMeta = new HashMap<>();
        logMeta.put("error", errorMessage);
        logMeta.put("path", request.getDescription(false));
        logger.error("Validation error", logMeta);
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Validation Error");
        errorResponse.setMessage(errorMessage);
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(Instant.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> logMeta = new HashMap<>();
        logMeta.put("error", ex.getMessage());
        logMeta.put("path", request.getDescription(false));
        logger.error("Unhandled exception", logMeta);
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setTimestamp(Instant.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}