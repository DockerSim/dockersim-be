package com.dockersim.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DockerfileFeedbackResponse {
    private String original; // Added field
    private String optimized; // Added field
    private String analysisMethod; // Added field
    private Long processingTimeMs; // Added field
    private String errorMessage; // Added field
    private String feedback; // Existing field
}
