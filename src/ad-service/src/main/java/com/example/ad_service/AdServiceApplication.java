package com.example.ad_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class AdServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(AdServiceApplication.class);

	public static void main(String[] args) {
		
		logger.info("Starting Ad Service Application...");

		SpringApplication.run(AdServiceApplication.class, args);

		logger.info("Ad Service Application started successfully");
	}

}
