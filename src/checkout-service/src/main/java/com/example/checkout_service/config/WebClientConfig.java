package com.example.checkout_service.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for WebClient beans used to communicate with external services.
 */
@Slf4j
@Configuration
public class WebClientConfig {
    
    @Value("${webclient.connect-timeout:3000}")
    private int connectTimeout;
    
    @Value("${webclient.read-timeout:5000}")
    private int readTimeout;
    
    @Value("${webclient.write-timeout:5000}")
    private int writeTimeout;
    
    /**
     * Creates a base WebClient with common configurations.
     */
    private WebClient.Builder createBaseWebClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                );
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse());
    }
    
    @Bean
    public WebClient cartServiceWebClient(ServiceProperties serviceProperties) {
        return createBaseWebClientBuilder()
                .baseUrl(serviceProperties.getCart().getFullUrl())
                .build();
    }
    
    @Bean
    public WebClient productCatalogServiceWebClient(ServiceProperties serviceProperties) {
        return createBaseWebClientBuilder()
                .baseUrl(serviceProperties.getProductCatalog().getFullUrl())
                .build();
    }
    
    @Bean
    public WebClient currencyServiceWebClient(ServiceProperties serviceProperties) {
        return createBaseWebClientBuilder()
                .baseUrl(serviceProperties.getCurrency().getFullUrl())
                .build();
    }
    
    @Bean
    public WebClient shippingServiceWebClient(ServiceProperties serviceProperties) {
        return createBaseWebClientBuilder()
                .baseUrl(serviceProperties.getShipping().getFullUrl())
                .build();
    }
    
    @Bean
    public WebClient paymentServiceWebClient(ServiceProperties serviceProperties) {
        return createBaseWebClientBuilder()
                .baseUrl(serviceProperties.getPayment().getFullUrl())
                .build();
    }
    
    @Bean
    public WebClient emailServiceWebClient(ServiceProperties serviceProperties) {
        return createBaseWebClientBuilder()
                .baseUrl(serviceProperties.getEmail().getFullUrl())
                .build();
    }
    
    /**
     * Filter to log outgoing requests.
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
    
    /**
     * Filter to log incoming responses.
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("Response status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}