package com.wallet.crypto_performance.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Configuraton {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
