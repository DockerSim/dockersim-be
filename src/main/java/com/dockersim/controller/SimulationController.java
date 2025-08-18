package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import com.dockersim.service.simulation.SimulationService;
import com.dockersim.util.IdConverter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService service;

    /**
     * 시뮬레이션 단건 조회
     *
     * @param userId       시뮬레이션 소유자 ID
     * @param simulationId 조회할 시뮬레이션의 ID
     * @return 시뮬레이션 정보 응답
     */
    @GetMapping("/{simulationId}")
    public ResponseEntity<ApiResponse<SimulationResponse>> getSimulation(
        /* @AuthenticationPrincipal */ Long userId,
        @PathVariable UUID simulationId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            service.getSimulation(userId, IdConverter.toLong(simulationId))));
    }

    /**
     * 새로운 시뮬레이션을 생성합니다.
     *
     * @param userId  시뮬레이션 소유자 ID
     * @param request 생성할 시뮬레이션의 정보 (이름, 공유 상태)
     * @return 생성된 시뮬레이션 정보 응답
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SimulationResponse>> createSimulation(
        /* @AuthenticationPrincipal */ Long userId,
        @RequestBody SimulationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            service.createSimulation(userId, request)));
    }

    /**
     * 특정 시뮬레이션을 수정합니다.
     *
     * @param userId       시뮬레이션 소유자 ID
     * @param simulationId 수정할 시뮬레이션의 ID
     * @param request      수정할 시뮬레이션의 정보 (이름, 공유 상태)
     * @return 수정된 시뮬레이션 정보 응답
     */
    @PutMapping("/{simulationId}")
    public ResponseEntity<ApiResponse<SimulationResponse>> updateSimulation(
        @PathVariable UUID simulationId,
        /* @AuthenticationPrincipal */ Long userId,
        @RequestBody SimulationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            service.updateSimulation(IdConverter.toLong(simulationId), userId, request)));
    }

    /**
     * 시뮬레이션을 삭제합니다. //     * @param userId       시뮬레이션 소유자 ID
     *
     * @param simulationId 삭제할 시뮬레이션의 ID
     */
    @DeleteMapping("/{simulationId}")
    public ResponseEntity<ApiResponse<Void>> deleteSimulation(
        /* @AuthenticationPrincipal */ Long userId,
        @PathVariable UUID simulationId
    ) {
        service.deleteSimulation(IdConverter.toLong(simulationId), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 특정 시뮬레이션에 협업자를 초대합니다.
     *
     * @param userId       시뮬레이션 소유자 ID
     * @param simulationId 협업자를 초대할 시뮬레이션의 ID
     * @param request      초대할 협업자의 정보 (이메일)
     * @return 초대된 협업자 정보 응답
     */
    @PostMapping("/{simulationId}/collaborators")
    public ResponseEntity<ApiResponse<CollaboratorResponse>> inviteCollaborator(
        /* @AuthenticationPrincipal */ Long userId,
        @PathVariable UUID simulationId,
        @RequestBody CollaboratorRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            service.inviteCollaborator(IdConverter.toLong(simulationId), userId,
                request)));
    }

    /**
     * 특정 시뮬레이션의 협업자 목록을 조회합니다.
     *
     * @param simulationId 협업자를 조회할 시뮬레이션의 ID
     * @param userId       요청자 ID
     * @return 협업자 목록 응답
     */
    @GetMapping("/{simulationId}/collaborators")
    public ResponseEntity<ApiResponse<List<CollaboratorResponse>>> getCollaborators(
        @PathVariable UUID simulationId,
        /* @AuthenticationPrincipal */ Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
            service.getCollaborators(IdConverter.toLong(simulationId), userId)));
    }

    /**
     * 특정 시뮬레이션의 협업자를 제거합니다.
     *
     * @param simulationId   협업자를 제거할 시뮬레이션의 ID
     * @param collaboratorId 제거할 협업자의 ID
     * @param userId         요청자 ID (소유자 또는 본인)
     */
    @DeleteMapping("/{simulationId}/collaborators/{collaboratorId}")
    public ResponseEntity<ApiResponse<Void>> removeCollaborator(
        @PathVariable UUID simulationId,
        @PathVariable UUID collaboratorId,
        /* @AuthenticationPrincipal */ Long userId) {
        service.removeCollaborator(
            IdConverter.toLong(simulationId),
            IdConverter.toLong(collaboratorId),
            userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
