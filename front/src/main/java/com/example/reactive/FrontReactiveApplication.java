package com.example.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class FrontReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontReactiveApplication.class, args);
	}
	@Bean
	WebClient webClient() {
		return WebClient.builder().build();
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
