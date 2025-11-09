package com.dockersim.service.compose;

import com.dockersim.domain.Simulation;
import com.dockersim.domain.User;
import com.dockersim.dto.request.ComposeGenerationRequest;
import com.dockersim.dto.request.ComposeGenerationRequest.InfrastructureData;
import com.dockersim.dto.response.ComposeGenerationResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.service.compose.ai.ComposePromptBuilder;
import com.dockersim.service.compose.ai.GeminiClient;
import com.dockersim.service.simulation.SimulationFinder;
import com.dockersim.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Docker Compose 생성 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DockerComposeServiceImpl implements DockerComposeService {

    private final GeminiClient geminiClient;
    private final ComposePromptBuilder promptBuilder;
    private final SimulationFinder simulationFinder;
    private final UserFinder userFinder;
    private final SimulationStateExtractor stateExtractor;

    @Override
    public ComposeGenerationResponse generateCompose(String userPublicId, String simulationPublicId) {
        long startTime = System.currentTimeMillis();

        log.info("Docker-compose 생성 시작 (자동 모드): userPublicId={}, simulationPublicId={}", userPublicId, simulationPublicId);

        try {
            // 사용자 및 시뮬레이션 검증
            User user = userFinder.findUserByPublicId(userPublicId);
            Simulation simulation = simulationFinder.findByPublicId(simulationPublicId);

            // 권한 확인 (읽기 권한만 있어도 compose 생성 가능)
            validateReadAccess(user, simulation);

            // 시뮬레이션의 현재 Docker 상태 추출
            InfrastructureData infraData = stateExtractor.extractInfrastructureData(simulationPublicId);
            log.debug("추출된 인프라 데이터: containers={}, images={}, networks={}, volumes={}",
                infraData.getContainers() != null ? infraData.getContainers().size() : 0,
                infraData.getImages() != null ? infraData.getImages().size() : 0,
                infraData.getNetworks() != null ? infraData.getNetworks().size() : 0,
                infraData.getVolumes() != null ? infraData.getVolumes().size() : 0);

            // 프롬프트 생성
            String prompt = promptBuilder.build(infraData);
            log.debug("생성된 프롬프트 길이: {}", prompt.length());

            // Gemini API 호출하여 compose 생성
            String composeContent = geminiClient.generateCompose(prompt);

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Docker-compose 생성 완료 (자동 모드): userPublicId={}, simulationPublicId={}, processingTime={}ms",
                userPublicId, simulationPublicId, processingTime);

            return ComposeGenerationResponse.builder()
                .composeContent(composeContent)
                .generationMethod("AI_AUTO")
                .processingTimeMs(processingTime)
                .build();

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Docker-compose 생성 실패 (자동 모드): userPublicId={}, simulationPublicId={}, error={}",
                userPublicId, simulationPublicId, e.getMessage(), e);

            return ComposeGenerationResponse.builder()
                .generationMethod("AI_AUTO")
                .processingTimeMs(processingTime)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    @Override
    public ComposeGenerationResponse generateComposeFromRequest(String userPublicId, String simulationPublicId, ComposeGenerationRequest request) {
        long startTime = System.currentTimeMillis();

        log.info("Docker-compose 생성 시작 (수동 모드): userPublicId={}, simulationPublicId={}", userPublicId, simulationPublicId);

        try {
            // 수동 모드에서는 검증을 모두 우회 (테스트 목적)
            log.debug("수동 모드 - 사용자 및 시뮬레이션 검증 우회: userPublicId={}", userPublicId);

            // 프롬프트 생성
            String prompt = promptBuilder.build(request.getInfrastructureData());
            log.debug("생성된 프롬프트 길이: {}", prompt.length());

            // Gemini API 호출하여 compose 생성
            String composeContent = geminiClient.generateCompose(prompt);

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Docker-compose 생성 완료 (수동 모드): userPublicId={}, simulationPublicId={}, processingTime={}ms",
                userPublicId, simulationPublicId, processingTime);

            return ComposeGenerationResponse.builder()
                .composeContent(composeContent)
                .generationMethod("AI_MANUAL")
                .processingTimeMs(processingTime)
                .build();

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Docker-compose 생성 실패 (수동 모드): userPublicId={}, simulationPublicId={}, error={}",
                userPublicId, simulationPublicId, e.getMessage(), e);

            return ComposeGenerationResponse.builder()
                .generationMethod("AI_MANUAL")
                .processingTimeMs(processingTime)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    private void validateReadAccess(User user, Simulation simulation) {
        // READ 상태인 경우 모든 사용자가 접근 가능
        if (simulation.getShareState().name().equals("READ")) {
            return;
        }

        // PRIVATE나 WRITE 상태인 경우 소유자이거나 협업자여야 함
        if (!simulation.isOwner(user) && !simulation.isCollaborator(user)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ACCESS_DENIED,
                user.getPublicId(), simulation.getPublicId());
        }
    }
}