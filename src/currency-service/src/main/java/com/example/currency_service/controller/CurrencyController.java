package com.example.currency_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.currency_service.model.ConversionResponse;
import com.example.currency_service.model.HealthResponse;
import com.example.currency_service.model.SupportedCurrenciesResponse;
import com.example.currency_service.service.CurrencyService;

import java.util.HashMap;
import java.util.Map;

/**
 * Currency REST Controller
 * HTTP request/response
 */
@RestController
public class CurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * GET / (Root)
     * API infos
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        logger.info("Request received: GET /");

        Map<String, Object> info = new HashMap<>();
        info.put("message", "Currency Service API");
        info.put("version", "1.0.0");
        info.put("description", "REST API for currency conversion");

        Map<String, Object> endpoints = new HashMap<>();

        Map<String, Object> currenciesEndpoint = new HashMap<>();
        currenciesEndpoint.put("path", "/currencies");
        currenciesEndpoint.put("method", "GET");
        currenciesEndpoint.put("description", "Get list of supported currencies");

        Map<String, Object> convertEndpoint = new HashMap<>();
        convertEndpoint.put("path", "/convert");
        convertEndpoint.put("method", "GET");
        convertEndpoint.put("description", "Convert currency");
        convertEndpoint.put("example", "/convert?from=USD&to=EUR&amount=100");

        Map<String, Object> healthEndpoint = new HashMap<>();
        healthEndpoint.put("path", "/health");
        healthEndpoint.put("method", "GET");
        healthEndpoint.put("description", "Health check endpoint");

        endpoints.put("currencies", currenciesEndpoint);
        endpoints.put("convert", convertEndpoint);
        endpoints.put("health", healthEndpoint);

        info.put("endpoints", endpoints);

        return ResponseEntity.ok(info);
    }

    /**
     * GET /currencies
     * get supported currencies
     */
    @GetMapping("/currencies")
    public ResponseEntity<SupportedCurrenciesResponse> getSupportedCurrencies() {
        logger.info("Request received: GET /api/currencies");

        SupportedCurrenciesResponse response = currencyService.getSupportedCurrencies();

        logger.info("Returning {} supported currencies");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /convert?from=USD&to=EUR&amount=100
     * conversion among units
     */
    @GetMapping("/convert")
    public ResponseEntity<ConversionResponse> convertCurrency(
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to,
            @RequestParam(name = "amount") double amount) {

        logger.info("Request received: GET /api/convert?from={}&to={}&amount={}", from, to, amount);

        // Validation: amount pozitif mi?
        if (amount <= 0) {
            logger.warn("Negative or zero amount: {}", amount);
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        ConversionResponse response = currencyService.convertCurrency(from, to, amount);

        logger.info("Conversion successful: {} {} = {} {}", amount, from, response.getResult(), to);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        logger.info("Request received: GET /api/health");

        HealthResponse response = new HealthResponse("OK", "Currency Service");
        return ResponseEntity.ok(response);
    }
}
