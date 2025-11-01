package com.example.cart_service.dto;

/**
 * Generic API response wrapper.
 * 
 * @param <T> The type of data contained in the response
 */
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;

    
    public ApiResponse() {
        this.success = true;
    }
    
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Constructor with success status, message, and data.
     * 
     * @param success Whether the operation was successful
     * @param message The response message
     * @param data The response data
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Static factory method to create a success response.
     * 
     * @param <T> The type of data
     * @param message The success message
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }
    
    /**
     * Static factory method to create a success response with data.
     * 
     * @param <T> The type of data
     * @param message The success message
     * @param data The response data
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    /**
     * Static factory method to create an error response.
     * 
     * @param <T> The type of data
     * @param message The error message
     * @return A new ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }
    
    /**
     * Gets whether the operation was successful.
     * 
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Sets whether the operation was successful.
     * 
     * @param success Whether the operation was successful
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * Gets the response message.
     * 
     * @return The response message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the response message.
     * 
     * @param message The response message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Gets the response data.
     * 
     * @return The response data
     */
    public T getData() {
        return data;
    }
    
    /**
     * Sets the response data.
     * 
     * @param data The response data to set
     */
    public void setData(T data) {
        this.data = data;
    }
}