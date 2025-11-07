package com.dockersim.service.compose;

import com.dockersim.dto.request.ComposeGenerationRequest.InfrastructureData;

import java.util.UUID;

/**
 * 시뮬레이션의 현재 Docker 상태를 추출하는 서비스 인터페이스
 */
public interface SimulationStateExtractor {

    /**
     * 시뮬레이션의 현재 Docker 상태를 추출하여 인프라 데이터로 변환합니다.
     *
     * @param simulationId 시뮬레이션 ID
     * @return 추출된 인프라 데이터
     */
    InfrastructureData extractInfrastructureData(UUID simulationId);
}