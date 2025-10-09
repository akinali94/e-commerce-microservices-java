package com.example.recommendation_service.dto;

public class ErrorResponse {
    
    @JsonProperty("error")
    private ErrorDetail error;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    public ErrorResponse() {
        this.timestamp = Instant.now().toString();
    }
    
    public ErrorResponse(ErrorDetail error) {
        this.error = error;
        this.timestamp = Instant.now().toString();
    }
    
    public ErrorResponse(String code, String message) {
        this.error = new ErrorDetail(code, message, null);
        this.timestamp = Instant.now().toString();
    }
    
    public ErrorDetail getError() {
        return error;
    }
    
    public void setError(ErrorDetail error) {
        this.error = error;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public static class ErrorDetail {
        
        @JsonProperty("code")
        private String code;
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("details")
        private Object details;
        
        public ErrorDetail() {
        }
        
        public ErrorDetail(String code, String message, Object details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Object getDetails() {
            return details;
        }
        
        public void setDetails(Object details) {
            this.details = details;
        }
    }
}
