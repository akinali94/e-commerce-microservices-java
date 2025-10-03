package com.example.payment_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceApplication.class);

	public static void main(String[] args) {

		logger.info("Starting Payment Service Application...");

		SpringApplication.run(PaymentServiceApplication.class, args);

		logger.info("Payment Service Application started successfully");

	}

}
