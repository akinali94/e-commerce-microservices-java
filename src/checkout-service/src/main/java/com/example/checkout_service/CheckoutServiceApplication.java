package com.example.checkout_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CheckoutServiceApplication {
	    private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceApplication.class);

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CheckoutServiceApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        logger.info("Application '{}' is running!\n|" +
                    "Local:      http://localhost:{}{}\n" +
                    "Started at: {}\n",
                    env.getProperty("spring.application.name"),
                    env.getProperty("server.port", "9555"),
                    env.getProperty("server.servlet.context-path", ""),
                    java.time.LocalDateTime.now());
	}

}
