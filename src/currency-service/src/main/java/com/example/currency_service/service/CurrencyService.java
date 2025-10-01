package com.example.currency_service.service;

import com.example.currency_service.model.ConversionResponse;
import com.example.currency_service.model.SupportedCurrenciesResponse;

public interface CurrencyService {
    SupportedCurrenciesResponse getSupportedCurrencies();
    ConversionResponse convertCurrency(String from, String to, double amount);
}
