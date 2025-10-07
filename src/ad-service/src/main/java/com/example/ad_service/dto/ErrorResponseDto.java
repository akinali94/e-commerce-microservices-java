package com.example.ad_service.dto;

import java.time.LocalDateTime;
import java.util.Objects;


public class ErrorResponseDto {
    private String error;
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDto(String error, String message, String code, String path) {
        this.error = error;
        this.message = message;
        this.code = code;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponseDto that = (ErrorResponseDto) o;
        return Objects.equals(error, that.error) &&
               Objects.equals(message, that.message) &&
               Objects.equals(code, that.code) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, message, code, timestamp, path);
    }

    @Override
    public String toString() {
        return "ErrorResponseDto{" +
               "error='" + error + '\'' +
               ", message='" + message + '\'' +
               ", code='" + code + '\'' +
               ", timestamp=" + timestamp +
               ", path='" + path + '\'' +
               '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String error;
        private String message;
        private String code;
        private String path;

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public ErrorResponseDto build() {
            return new ErrorResponseDto(error, message, code, path);
        }
    }
}