package it.visualsoftware.notificator.RestTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
	
	@Bean 
    public RestTemplate restTemplate() {
        RestTemplate rest = new RestTemplate();
        return rest;
    }
}
