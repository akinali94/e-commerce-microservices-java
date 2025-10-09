package com.example.checkout_service.util;

import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.UUID;


public class LoggingUtil {
    
    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String ORDER_ID = "orderId";
    
    private LoggingUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Generates and sets a request ID in MDC for tracing.
     * 
     * @return the generated request ID
     */
    public static String generateAndSetRequestId() {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);
        return requestId;
    }
    
    /**
     * Sets the request ID in MDC.
     * 
     * @param requestId the request ID to set
     */
    public static void setRequestId(String requestId) {
        if (requestId != null && !requestId.isEmpty()) {
            MDC.put(REQUEST_ID, requestId);
        }
    }
    
    /**
     * Gets the request ID from MDC.
     * 
     * @return the request ID, or null if not set
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }
    
    /**
     * Sets the user ID in MDC for logging.
     * 
     * @param userId the user ID
     */
    public static void setUserId(String userId) {
        if (userId != null && !userId.isEmpty()) {
            MDC.put(USER_ID, userId);
        }
    }
    
    /**
     * Gets the user ID from MDC.
     * 
     * @return the user ID, or null if not set
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }
    
    /**
     * Sets the order ID in MDC for logging.
     * 
     * @param orderId the order ID
     */
    public static void setOrderId(String orderId) {
        if (orderId != null && !orderId.isEmpty()) {
            MDC.put(ORDER_ID, orderId);
        }
    }
    
    /**
     * Gets the order ID from MDC.
     * 
     * @return the order ID, or null if not set
     */
    public static String getOrderId() {
        return MDC.get(ORDER_ID);
    }
    
    /**
     * Clears all MDC context.
     */
    public static void clearMDC() {
        MDC.clear();
    }
    
    /**
     * Logs the start of an operation.
     * 
     * @param logger the logger to use
     * @param operation the operation name
     * @param params additional parameters
     */
    public static void logOperationStart(Logger logger, String operation, Object... params) {
        logger.info("Starting operation: {} with params: {}", operation, params);
    }
    
    /**
     * Logs the end of an operation.
     * 
     * @param logger the logger to use
     * @param operation the operation name
     * @param durationMs the duration in milliseconds
     */
    public static void logOperationEnd(Logger logger, String operation, long durationMs) {
        logger.info("Completed operation: {} in {}ms", operation, durationMs);
    }
    
    /**
     * Logs an external service call.
     * 
     * @param logger the logger to use
     * @param serviceName the service name
     * @param operation the operation being called
     */
    public static void logExternalServiceCall(Logger logger, String serviceName, String operation) {
        logger.debug("Calling external service: {} - operation: {}", serviceName, operation);
    }
    
    /**
     * Logs a successful external service response.
     * 
     * @param logger the logger to use
     * @param serviceName the service name
     * @param durationMs the duration in milliseconds
     */
    public static void logExternalServiceSuccess(Logger logger, String serviceName, long durationMs) {
        logger.debug("External service {} responded successfully in {}ms", serviceName, durationMs);
    }
    
    /**
     * Logs a failed external service call.
     * 
     * @param logger the logger to use
     * @param serviceName the service name
     * @param error the error message
     */
    public static void logExternalServiceError(Logger logger, String serviceName, String error) {
        logger.error("External service {} failed: {}", serviceName, error);
    }
}