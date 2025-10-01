package com.example.currency_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class CurrencyServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceApplication.class);

	public static void main(String[] args) {
		
		logger.info("Starting Currency Service Application...");

		SpringApplication.run(CurrencyServiceApplication.class, args);

		logger.info("Currency Service Application started successfully");
	}

}
