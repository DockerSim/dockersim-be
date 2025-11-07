package com.dockersim.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Gemini API 설정
 */
@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.timeout:30}")
    private int timeoutSeconds;

    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
            .baseUrl(apiUrl)
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("x-goog-api-key", apiKey)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }
}