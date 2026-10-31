package com.mar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestApiConfiguration {

    @Bean
    public RestTemplate sourceDbRestClient() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate targetDbRestClient() {
        return new RestTemplate();
    }


}
