package ru.netology.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JavaConfig {

    @Value("${ORIGIN_HOSTS:https://serp-ya.github.io, http://localhost:3000}")
    private String[] origins;

    @Bean
    public WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/transfer").allowedOrigins(origins);
                registry.addMapping("/confirmOperation").allowedOrigins(origins);
            }
        };
    }
}
