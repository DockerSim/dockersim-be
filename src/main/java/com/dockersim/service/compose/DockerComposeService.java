package com.dockersim.service.compose;

import com.dockersim.dto.request.ComposeGenerationRequest;
import com.dockersim.dto.response.ComposeGenerationResponse;

/**
 * Docker Compose 생성 서비스 인터페이스
 */
public interface DockerComposeService {

    /**
     * 시뮬레이션의 현재 Docker 상태를 바탕으로 docker-compose.yml 파일을 생성합니다.
     *
     * @param userPublicId 사용자 Public ID
     * @param simulationPublicId 시뮬레이션 Public ID
     * @return compose 생성 응답
     */
    ComposeGenerationResponse generateCompose(String userPublicId, String simulationPublicId);

    /**
     * 제공된 인프라 정보를 바탕으로 docker-compose.yml 파일을 생성합니다. (수동 모드)
     *
     * @param userPublicId 사용자 Public ID
     * @param simulationPublicId 시뮬레이션 Public ID
     * @param request compose 생성 요청
     * @return compose 생성 응답
     */
    ComposeGenerationResponse generateComposeFromRequest(String userPublicId, String simulationPublicId, ComposeGenerationRequest request);
}