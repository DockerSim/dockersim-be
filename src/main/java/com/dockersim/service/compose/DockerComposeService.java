package com.dockersim.service.compose;

import com.dockersim.dto.request.ComposeGenerationRequest;
import com.dockersim.dto.response.ComposeGenerationResponse;

import java.util.UUID;

/**
 * Docker Compose 생성 서비스 인터페이스
 */
public interface DockerComposeService {

    /**
     * 시뮬레이션의 현재 Docker 상태를 바탕으로 docker-compose.yml 파일을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param simulationId 시뮬레이션 ID
     * @return compose 생성 응답
     */
    ComposeGenerationResponse generateCompose(UUID userId, UUID simulationId);

    /**
     * 제공된 인프라 정보를 바탕으로 docker-compose.yml 파일을 생성합니다. (수동 모드)
     *
     * @param userId 사용자 ID
     * @param simulationId 시뮬레이션 ID
     * @param request compose 생성 요청
     * @return compose 생성 응답
     */
    ComposeGenerationResponse generateComposeFromRequest(UUID userId, UUID simulationId, ComposeGenerationRequest request);
}