package com.example.shipping_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggerUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Logs a structured JSON message
     */
    private void log(String severity, String message, Map<String, Object> meta) {
        try {
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("timestamp", Instant.now().toString());
            logEntry.put("severity", severity);
            logEntry.put("message", message);
            
            if (meta != null && !meta.isEmpty()) {
                logEntry.putAll(meta);
            }
            
            String jsonLog = objectMapper.writeValueAsString(logEntry);
            
            switch (severity.toLowerCase()) {
                case "debug":
                    logger.debug(jsonLog);
                    break;
                case "info":
                    logger.info(jsonLog);
                    break;
                case "warn":
                    logger.warn(jsonLog);
                    break;
                case "error":
                    logger.error(jsonLog);
                    break;
                case "fatal":
                    logger.error(jsonLog);
                    break;
                default:
                    logger.info(jsonLog);
            }
        } catch (Exception e) {
            logger.error("Error creating structured log", e);
        }
    }
    
    public void debug(String message) {
        log("debug", message, null);
    }
    
    public void debug(String message, Map<String, Object> meta) {
        log("debug", message, meta);
    }
    
    public void info(String message) {
        log("info", message, null);
    }
    
    public void info(String message, Map<String, Object> meta) {
        log("info", message, meta);
    }
    
    public void warn(String message) {
        log("warn", message, null);
    }
    
    public void warn(String message, Map<String, Object> meta) {
        log("warn", message, meta);
    }
    
    public void error(String message) {
        log("error", message, null);
    }
    
    public void error(String message, Map<String, Object> meta) {
        log("error", message, meta);
    }
    
    public void fatal(String message) {
        log("fatal", message, null);
    }
    
    public void fatal(String message, Map<String, Object> meta) {
        log("fatal", message, meta);
    }
}