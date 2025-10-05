package com.example.shipping_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import com.example.shipping_service.util.LoggerUtil;

@SpringBootApplication
public class ShippingServiceApplication {

    private final LoggerUtil logger;
    private final Environment env;
    
    public ShippingServiceApplication(LoggerUtil logger, Environment env) {
        this.logger = logger;
        this.env = env;
    }

	public static void main(String[] args) {
		SpringApplication.run(ShippingServiceApplication.class, args);
	}

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = env.getProperty("server.port", "50051");
        
        // Log configuration (matching Go service)
        String disableTracing = env.getProperty("DISABLE_TRACING", "");
        String disableProfiler = env.getProperty("DISABLE_PROFILER", "");
        String disableStats = env.getProperty("DISABLE_STATS", "");
        
        if (!disableTracing.isEmpty()) {
            logger.info("Tracing disabled.");
        } else {
            logger.info("Tracing enabled, but temporarily unavailable");
        }
        
        if (!disableProfiler.isEmpty()) {
            logger.info("Profiling disabled.");
        } else {
            logger.info("Profiling enabled.");
        }
        
        if (!disableStats.isEmpty()) {
            logger.info("Stats disabled.");
        } else {
            logger.info("Stats enabled, but temporarily unavailable");
        }
        
        logger.info("Shipping Service listening on port " + port);
    }

}
