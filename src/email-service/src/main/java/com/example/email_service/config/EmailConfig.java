package com.example.email_service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Bean
    @ConditionalOnProperty(name = "email.service.dummy-mode", havingValue = "true")
    public boolean useDummyMode() {
        return true;
    }
}