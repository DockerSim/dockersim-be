package com.dockersim.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Dockerfile 피드백 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dockerfile 피드백 요청")
public class DockerfileFeedbackRequest {

    @Schema(description = "분석할 Dockerfile 내용", example = "FROM node:18\nWORKDIR /app\nCOPY . .\nRUN npm install\nEXPOSE 3000\nCMD [\"npm\", \"start\"]")
    @NotBlank(message = "Dockerfile 내용은 필수입니다")
    private String dockerfileContent;
}