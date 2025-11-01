package com.example.checkout_service.service.client;

import com.example.checkout_service.model.Money;
import com.example.checkout_service.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyServiceClient implements CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceClient.class);

    private final RestTemplate restTemplate;
    private final String currencyServiceUrl;

    public CurrencyServiceClient(RestTemplate restTemplate, @Value("${services.currency.url}") String currencyServiceUrl) {
        this.restTemplate = restTemplate;
        this.currencyServiceUrl = currencyServiceUrl;
    }

    @Override
    public Money convertCurrency(Money from, String toCurrency) {
        logger.info("Converting currency from {} to {}", from.getCurrencyCode(), toCurrency);
        
        return restTemplate.postForObject(
                currencyServiceUrl + "/currency-conversion?toCurrency=" + toCurrency,
                from, 
                Money.class);
    }
}