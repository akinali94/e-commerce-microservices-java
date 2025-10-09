package com.example.checkout_service.util;

import java.util.UUID;

/**
 * Utility class for UUID generation.
 */
public class UuidUtil {
    
    private UuidUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Generates a random UUID string.
     * 
     * @return UUID string
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generates a random UUID.
     * 
     * @return UUID object
     */
    public static UUID generateUuidObject() {
        return UUID.randomUUID();
    }
    
    /**
     * Validates if a string is a valid UUID.
     * 
     * @param uuid the UUID string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUuid(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return false;
        }
        
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
