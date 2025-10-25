package com.example.currency_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class CurrencyServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceApplication.class);

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CurrencyServiceApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        logger.info("Application '{}' is running!\n" +
                    "Local:      http://localhost:{}{}\n" +
                    "Started at: {}\n",
                    env.getProperty("spring.application.name"),
                    env.getProperty("server.port", "9555"),
                    env.getProperty("server.servlet.context-path", ""),
                    java.time.LocalDateTime.now());
    }
}
