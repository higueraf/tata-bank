package com.tata.account.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    private final Dotenv dotenv;

    @Autowired
    public JwtConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @Bean
    public String jwtSecret() {
        return dotenv.get("JWT_SECRET_KEY", "defaultSecretKey");
    }

    @Bean
    public long jwtExpiration() {
        return Long.parseLong(dotenv.get("JWT_EXPIRATION", "86400000")); // 8 hours in milliseconds by default
    }
}
