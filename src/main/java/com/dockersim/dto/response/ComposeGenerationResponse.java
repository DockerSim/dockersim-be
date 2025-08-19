package com.dockersim.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Docker-compose 생성 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Docker-compose 생성 응답")
public class ComposeGenerationResponse {

    @Schema(description = "생성된 docker-compose.yml 내용")
    private String composeContent;

    @Schema(description = "생성 방식", example = "AI")
    private String generationMethod;

    @Schema(description = "생성 시간")
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Schema(description = "처리 시간 (밀리초)")
    private Long processingTimeMs;

    @Schema(description = "생성 성공 여부")
    @Builder.Default
    private Boolean success = true;

    @Schema(description = "오류 메시지 (실패시)")
    private String errorMessage;
}