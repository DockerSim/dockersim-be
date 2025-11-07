package com.dockersim.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Dockerfile 피드백 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dockerfile 피드백 응답")
public class DockerfileFeedbackResponse {

    @Schema(description = "원본 Dockerfile 내용")
    private String original;

    @Schema(description = "최적화된 Dockerfile 내용 (주석 포함)")
    private String optimized;

    @Schema(description = "분석 방식", example = "AI")
    private String analysisMethod;

    @Schema(description = "분석 시간")
    @Builder.Default
    private LocalDateTime analyzedAt = LocalDateTime.now();

    @Schema(description = "처리 시간 (밀리초)")
    private Long processingTimeMs;

    @Schema(description = "오류 메시지 (실패시)")
    private String errorMessage;
}