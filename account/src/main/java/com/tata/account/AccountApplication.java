package com.tata.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AccountApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AccountApplication.class)
				.properties("server.port=8090")
				.run(args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
