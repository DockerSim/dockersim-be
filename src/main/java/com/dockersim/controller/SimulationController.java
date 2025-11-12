package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.AuthErrorCode;
import com.dockersim.service.simulation.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "시뮬레이션 API", description = "시뮬레이션 생성, 조회, 수정, 삭제 및 협업자 관리 API")
@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
@Slf4j
public class SimulationController {

    private final SimulationService service;

    @Operation(summary = "시뮬레이션 조회", description = "시뮬레이션을 조회합니다. READ 상태인 경우 모든 사용자가 조회 가능합니다.")
    @GetMapping("/{simulationPublicId}")
    public ResponseEntity<ApiResponse<SimulationResponse>> getSimulation(
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal,
            @Parameter(description = "조회할 시뮬레이션 ID") @PathVariable String simulationPublicId
    ) {
        String userPublicId = (principal != null) ? principal.getUserPublicId() : null;
        return ResponseEntity.ok(ApiResponse.success(
                service.getSimulation(userPublicId, simulationPublicId)));
    }

    @Operation(summary = "시뮬레이션 생성", description = "새로운 시뮬레이션을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<SimulationResponse>> createSimulation(
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal,
            @Parameter(description = "생성할 시뮬레이션 정보") @RequestBody SimulationRequest request
    ) {
        if (principal == null || principal.getUserPublicId() == null) {
            log.error("Authentication principal is null or userPublicId is null. Cannot create simulation.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        String userPublicId = principal.getUserPublicId();
        log.info("Attempting to create simulation for userPublicId: {}", userPublicId);
        
        return ResponseEntity.ok(ApiResponse.success(
                service.createSimulation(userPublicId, request)));
    }

    @Operation(summary = "시뮬레이션 수정", description = "시뮬레이션의 제목과 공유 상태를 수정합니다. WRITE에서 다른 상태로 변경 시 모든 협업자가 제거됩니다.")
    @PutMapping("/{simulationPublicId}")
    public ResponseEntity<ApiResponse<SimulationResponse>> updateSimulation(
            @Parameter(description = "수정할 시뮬레이션 ID") @PathVariable String simulationPublicId,
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal,
            @Parameter(description = "수정할 시뮬레이션 정보") @RequestBody SimulationRequest request
    ) {
        if (principal == null || principal.getUserPublicId() == null) {
            log.error("Authentication principal is null or userPublicId is null. Cannot update simulation.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        String userPublicId = principal.getUserPublicId();
        log.info("Attempting to update simulation {} for userPublicId: {}", simulationPublicId, userPublicId);
        
        return ResponseEntity.ok(ApiResponse.success(
                service.updateSimulation(userPublicId, simulationPublicId, request)));
    }

    @Operation(summary = "시뮬레이션 삭제", description = "시뮬레이션을 삭제합니다. 소유자만 삭제할 수 있습니다.")
    @DeleteMapping("/{simulationPublicId}")
    public ResponseEntity<ApiResponse<Void>> deleteSimulation(
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal,
            @Parameter(description = "삭제할 시뮬레이션 ID") @PathVariable String simulationPublicId
    ) {
        if (principal == null || principal.getUserPublicId() == null) {
            log.error("Authentication principal is null or userPublicId is null. Cannot delete simulation.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        service.deleteSimulation(principal.getUserPublicId(), simulationPublicId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "현재 사용자의 모든 시뮬레이션 조회", description = "현재 로그인한 사용자의 모든 시뮬레이션 목록을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<SimulationResponse>>> getMySimulations(
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal
    ) {
        if (principal == null) { // principal이 null인 경우를 명확히 로그
            log.error("Authentication principal is null. Cannot get user's simulations.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (principal.getUserPublicId() == null) { // userPublicId가 null인 경우를 명확히 로그
            log.error("userPublicId is null in principal. Cannot get user's simulations.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        String userPublicId = principal.getUserPublicId();
        log.info("Attempting to get simulations for userPublicId: {}", userPublicId);
        return ResponseEntity.ok(ApiResponse.success(
                service.getMySimulations(userPublicId)));
    }

    @Operation(summary = "협업자 초대", description = "시뮬레이션에 협업자를 초대합니다. 최대 6명까지 초대할 수 있습니다.")
    @PostMapping("/{simulationPublicId}/collaborators")
    public ResponseEntity<ApiResponse<CollaboratorResponse>> inviteCollaborator(
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal,
            @Parameter(description = "협업자를 초대할 시뮬레이션 ID") @PathVariable String simulationPublicId,
            @Parameter(description = "초대할 협업자 정보") @RequestBody CollaboratorRequest request
    ) {
        if (principal == null || principal.getUserPublicId() == null) {
            log.error("Authentication principal is null or userPublicId is null. Cannot invite collaborator.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        return ResponseEntity.ok(ApiResponse.success(
                service.inviteCollaborator(simulationPublicId, principal.getUserPublicId(), request)));
    }

    @Operation(summary = "협업자 목록 조회", description = "시뮬레이션의 협업자 목록을 조회합니다.")
    @GetMapping("/{simulationPublicId}/collaborators")
    public ResponseEntity<ApiResponse<List<CollaboratorResponse>>> getCollaborators(
            @Parameter(description = "협업자를 조회할 시뮬레이션 ID") @PathVariable String simulationPublicId,
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal
    ) {
        if (principal == null || principal.getUserPublicId() == null) {
            log.error("Authentication principal is null or userPublicId is null. Cannot get collaborators.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        return ResponseEntity.ok(ApiResponse.success(
                service.getCollaborators(principal.getUserPublicId(), simulationPublicId)));
    }

    @Operation(summary = "협업자 제거", description = "시뮬레이션에서 협업자를 제거합니다. 소유자만 제거할 수 있습니다.")
    @DeleteMapping("/{simulationPublicId}/collaborators/{collaboratorId}")
    public ResponseEntity<ApiResponse<Void>> removeCollaborator(
            @Parameter(description = "협업자를 제거할 시뮬레이션 ID") @PathVariable String simulationPublicId,
            @Parameter(description = "제거할 협업자 ID") @PathVariable String collaboratorId,
            @Parameter(hidden = true) @AuthenticationPrincipal SimulationUserPrincipal principal
    ) {
        if (principal == null || principal.getUserPublicId() == null) {
            log.error("Authentication principal is null or userPublicId is null. Cannot remove collaborator.");
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        service.removeCollaborator(principal.getUserPublicId(), simulationPublicId, collaboratorId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
