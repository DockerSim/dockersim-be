package com.dockersim.service.compose.ai;

import com.dockersim.exception.GeminiApiException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Gemini API 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient geminiWebClient;

    public String generateCompose(String prompt) {
        try {
            log.debug("Gemini API 호출 시작: prompt length = {}", prompt.length());
            
            GeminiRequest request = GeminiRequest.builder()
                .contents(List.of(
                    Content.builder()
                        .parts(List.of(
                            Part.builder()
                                .text(prompt)
                                .build()
                        ))
                        .build()
                ))
                .generationConfig(GenerationConfig.builder()
                    .temperature(0.1)
                    .maxOutputTokens(8192)
                    .build())
                .build();

            GeminiResponse response = geminiWebClient
                .post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .block();

            String composeYml = extractComposeContent(response);
            log.debug("Gemini API 호출 성공: response length = {}", composeYml.length());
            
            return composeYml;

        } catch (WebClientResponseException e) {
            log.error("Gemini API 호출 실패: status = {}, body = {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GeminiApiException("Gemini API 호출 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생", e);
            throw new GeminiApiException("Gemini API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public String analyzeDockerfile(String prompt) {
        try {
            log.debug("Dockerfile 분석 API 호출 시작: prompt length = {}", prompt.length());
            
            GeminiRequest request = GeminiRequest.builder()
                .contents(List.of(
                    Content.builder()
                        .parts(List.of(
                            Part.builder()
                                .text(prompt)
                                .build()
                        ))
                        .build()
                ))
                .generationConfig(GenerationConfig.builder()
                    .temperature(0.1)
                    .maxOutputTokens(4096)
                    .build())
                .build();

            GeminiResponse response = geminiWebClient
                .post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .timeout(Duration.ofSeconds(30))
                .block();

            String dockerfileContent = extractDockerfileContent(response);
            log.debug("Dockerfile 분석 API 호출 성공: response length = {}", dockerfileContent.length());
            
            return dockerfileContent;

        } catch (WebClientResponseException e) {
            log.error("Dockerfile 분석 API 호출 실패: status = {}, body = {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GeminiApiException("Dockerfile 분석 API 호출 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Dockerfile 분석 API 호출 중 오류 발생", e);
            throw new GeminiApiException("Dockerfile 분석 API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private String extractComposeContent(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            throw new GeminiApiException("Gemini API 응답이 비어있습니다");
        }

        Candidate candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null || candidate.getContent().getParts() == null || 
            candidate.getContent().getParts().isEmpty()) {
            throw new GeminiApiException("Gemini API 응답 내용이 비어있습니다");
        }

        String content = candidate.getContent().getParts().get(0).getText();
        
        // ```yaml 코드 블록에서 내용 추출
        if (content.contains("```yaml")) {
            content = content.substring(content.indexOf("```yaml") + 7);
            if (content.contains("```")) {
                content = content.substring(0, content.indexOf("```"));
            }
        } else if (content.contains("```")) {
            content = content.substring(content.indexOf("```") + 3);
            if (content.contains("```")) {
                content = content.substring(0, content.indexOf("```"));
            }
        }

        return content.trim();
    }

    private String extractDockerfileContent(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            throw new GeminiApiException("Gemini API 응답이 비어있습니다");
        }

        Candidate candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null || candidate.getContent().getParts() == null || 
            candidate.getContent().getParts().isEmpty()) {
            throw new GeminiApiException("Gemini API 응답 내용이 비어있습니다");
        }

        String content = candidate.getContent().getParts().get(0).getText();
        
        // ```dockerfile 코드 블록에서 내용 추출
        if (content.contains("```dockerfile")) {
            content = content.substring(content.indexOf("```dockerfile") + 13);
            if (content.contains("```")) {
                content = content.substring(0, content.indexOf("```"));
            }
        } else if (content.contains("```")) {
            content = content.substring(content.indexOf("```") + 3);
            if (content.contains("```")) {
                content = content.substring(0, content.indexOf("```"));
            }
        }

        return content.trim();
    }

    // Gemini API 요청/응답 DTO 클래스들
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeminiRequest {
        private List<Content> contents;
        private GenerationConfig generationConfig;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part {
        private String text;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GenerationConfig {
        private Double temperature;
        private Integer maxOutputTokens;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeminiResponse {
        private List<Candidate> candidates;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Integer index;
    }
}