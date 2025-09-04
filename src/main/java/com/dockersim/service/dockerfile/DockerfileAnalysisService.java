package com.dockersim.service.dockerfile;

import com.dockersim.dto.request.DockerfileFeedbackRequest;
import com.dockersim.dto.response.DockerfileFeedbackResponse;
import com.dockersim.service.compose.ai.GeminiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Dockerfile 분석 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DockerfileAnalysisService {

    private final GeminiClient geminiClient;
    private final DockerfilePromptBuilder promptBuilder;

    public DockerfileFeedbackResponse analyzeDockerfile(DockerfileFeedbackRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Dockerfile 분석 시작: content length = {}", request.getDockerfileContent().length());
            
            // Gemini API 프롬프트 생성
            String prompt = promptBuilder.buildAnalysisPrompt(request.getDockerfileContent());
            
            // Gemini API 호출하여 분석 결과 받기
            String optimizedDockerfile = geminiClient.analyzeDockerfile(prompt);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            log.info("Dockerfile 분석 완료: processing time = {}ms", processingTime);
            
            return DockerfileFeedbackResponse.builder()
                    .original(request.getDockerfileContent())
                    .optimized(optimizedDockerfile)
                    .analysisMethod("AI")
                    .processingTimeMs(processingTime)
                    .build();
                    
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            
            log.error("Dockerfile 분석 실패: processing time = {}ms", processingTime, e);
            
            return DockerfileFeedbackResponse.builder()
                    .original(request.getDockerfileContent())
                    .analysisMethod("AI")
                    .processingTimeMs(processingTime)
                    .errorMessage("Dockerfile 분석 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
}