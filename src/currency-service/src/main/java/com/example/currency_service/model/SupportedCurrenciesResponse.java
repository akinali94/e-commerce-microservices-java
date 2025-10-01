package com.example.currency_service.model;

import java.util.List;

public class SupportedCurrenciesResponse {
    private List<String> currencyCodes;

    public SupportedCurrenciesResponse(){

    }

    public SupportedCurrenciesResponse(List<String> currencyCodes){
        this.currencyCodes = currencyCodes;
    }

    public List<String> getCurrencyCode(){
        return currencyCodes;
    }

    public void setCurrencyCodes(List<String> currencyCodes){
        this.currencyCodes = currencyCodes;
    }
}
