package com.dockersim.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Gemini API 설정
 */
@Slf4j
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
        String urlWithKey = apiUrl + "?key=" + apiKey;
        log.info("=== Gemini WebClient 초기화 ===");
        log.info("Base URL (with key): {}", apiUrl + "?key=" + maskApiKey(apiKey));
        log.info("Timeout: {} seconds", timeoutSeconds);

        return WebClient.builder()
            .baseUrl(urlWithKey)
            .defaultHeader("Content-Type", "application/json")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }

    private String maskApiKey(String key) {
        if (key == null || key.length() < 8) {
            return "****";
        }
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }
}