package com.example.ad_service.exception;

import com.hipstershop.adservice.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(AdServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleAdServiceException(
            AdServiceException ex, 
            WebRequest request) {
        
        logger.error("AdServiceException: {}", ex.getMessage(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .code(ex.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRequestException(
            InvalidRequestException ex, 
            WebRequest request) {
        
        logger.warn("InvalidRequestException: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("InvalidRequestException")
                .message(ex.getMessage())
                .code(ex.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(
            NoHandlerFoundException ex, 
            WebRequest request) {
        
        logger.warn("NoHandlerFoundException: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("NotFound")
                .message("Route " + ex.getHttpMethod() + " " + ex.getRequestURL() + " not found")
                .code("NOT_FOUND")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, 
            WebRequest request) {
        
        logger.error("Unexpected exception: {}", ex.getMessage(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("InternalServerError")
                .message("An unexpected error occurred")
                .code("INTERNAL_SERVER_ERROR")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            WebRequest request) {
        
        logger.warn("IllegalArgumentException: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("BadRequest")
                .message(ex.getMessage())
                .code("INVALID_ARGUMENT")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}