package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.request.ComposeGenerationRequest;
import com.dockersim.dto.response.ComposeGenerationResponse;
import com.dockersim.service.compose.DockerComposeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Docker Compose 생성 컨트롤러
 */
@Tag(name = "Docker Compose API", description = "Docker Compose 파일 생성 API")
@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class DockerComposeController {

    private final DockerComposeService dockerComposeService;

    @Operation(
        summary = "Docker-compose 파일 생성 (자동 모드)",
        description = """
            시뮬레이션의 현재 Docker 상태를 바탕으로 최적화된 docker-compose.yml 파일을 자동 생성합니다.
            
            ### 권한:
            - READ 상태: 모든 사용자 접근 가능
            - PRIVATE/WRITE 상태: 소유자 및 협업자만 접근 가능
            
            ### 생성 과정:
            1. 시뮬레이션의 현재 Docker 상태 자동 추출 (컨테이너, 이미지, 네트워크, 볼륨)
            2. 추출된 정보를 분석하여 프롬프트 생성
            3. Gemini AI를 통해 최적화된 docker-compose.yml 생성
            4. 프로덕션 환경에 적합한 설정 포함 (보안, 리소스 제한, 헬스체크 등)
            """
    )
    @PostMapping("/{simulationId}/compose")
    public ResponseEntity<ApiResponse<ComposeGenerationResponse>> generateCompose(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "시뮬레이션 ID", example = "123e4567-e89b-12d3-a456-426614174000") 
        @PathVariable UUID simulationId
    ) {
        ComposeGenerationResponse response = dockerComposeService.generateCompose(userId, simulationId);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                com.dockersim.exception.code.ComposeErrorCode.COMPOSE_GENERATION_FAILED,
                "Docker-compose 생성에 실패했습니다: " + response.getErrorMessage()
            ));
        }
    }

    @Operation(
        summary = "Docker-compose 파일 생성 (수동 모드)",
        description = """
            제공된 인프라 정보를 바탕으로 최적화된 docker-compose.yml 파일을 생성합니다.
            
            ### 권한:
            - READ 상태: 모든 사용자 접근 가능
            - PRIVATE/WRITE 상태: 소유자 및 협업자만 접근 가능
            
            ### 생성 과정:
            1. 제공된 인프라 정보를 분석하여 프롬프트 생성
            2. Gemini AI를 통해 최적화된 docker-compose.yml 생성
            3. 프로덕션 환경에 적합한 설정 포함 (보안, 리소스 제한, 헬스체크 등)
            """
    )
    @PostMapping("/{simulationId}/compose/manual")
    public ResponseEntity<ApiResponse<ComposeGenerationResponse>> generateComposeManual(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "시뮬레이션 ID", example = "123e4567-e89b-12d3-a456-426614174000") 
        @PathVariable UUID simulationId,
        @Parameter(description = "Docker-compose 생성 요청 정보")
        @Valid @RequestBody ComposeGenerationRequest request
    ) {
        ComposeGenerationResponse response = dockerComposeService.generateComposeFromRequest(userId, simulationId, request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                com.dockersim.exception.code.ComposeErrorCode.COMPOSE_GENERATION_FAILED,
                "Docker-compose 생성에 실패했습니다: " + response.getErrorMessage()
            ));
        }
    }
}