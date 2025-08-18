package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import com.dockersim.service.simulation.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "시뮬레이션 API", description = "시뮬레이션 생성, 조회, 수정, 삭제 및 협업자 관리 API")
@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService service;

    @Operation(summary = "시뮬레이션 조회", description = "시뮬레이션을 조회합니다. READ 상태인 경우 모든 사용자가 조회 가능합니다.")
    @GetMapping("/{simulationId}")
    public ResponseEntity<ApiResponse<SimulationResponse>> getSimulation(
        @Parameter(hidden = true) /* @AuthenticationPrincipal */ UUID userId,
        @Parameter(description = "조회할 시뮬레이션 ID") @PathVariable UUID simulationId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            service.getSimulation(userId, simulationId)));
    }

    @Operation(summary = "시뮬레이션 생성", description = "새로운 시뮬레이션을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<SimulationResponse>> createSimulation(
        @Parameter(hidden = true) /* @AuthenticationPrincipal */ UUID userId,
        @Parameter(description = "생성할 시뮬레이션 정보") @RequestBody SimulationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            service.createSimulation(userId, request)));
    }

    @Operation(summary = "시뮬레이션 수정", description = "시뮬레이션의 제목과 공유 상태를 수정합니다. WRITE에서 다른 상태로 변경 시 모든 협업자가 제거됩니다.")
    @PutMapping("/{simulationId}")
    public ResponseEntity<ApiResponse<SimulationResponse>> updateSimulation(
        @Parameter(description = "수정할 시뮬레이션 ID") @PathVariable UUID simulationId,
        @Parameter(hidden = true) /* @AuthenticationPrincipal */ UUID userId,
        @Parameter(description = "수정할 시뮬레이션 정보") @RequestBody SimulationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            service.updateSimulation(userId, simulationId, request)));
    }

    @Operation(summary = "시뮬레이션 삭제", description = "시뮬레이션을 삭제합니다. 소유자만 삭제할 수 있습니다.")
    @DeleteMapping("/{simulationId}")
    public ResponseEntity<ApiResponse<Void>> deleteSimulation(
        @Parameter(hidden = true) /* @AuthenticationPrincipal */ UUID userId,
        @Parameter(description = "삭제할 시뮬레이션 ID") @PathVariable UUID simulationId
    ) {
        service.deleteSimulation(userId, simulationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "협업자 초대", description = "시뮬레이션에 협업자를 초대합니다. 최대 6명까지 초대할 수 있습니다.")
    @PostMapping("/{simulationId}/collaborators")
    public ResponseEntity<ApiResponse<CollaboratorResponse>> inviteCollaborator(
        @Parameter(hidden = true) /* @AuthenticationPrincipal */ UUID userId,
        @Parameter(description = "협업자를 초대할 시뮬레이션 ID") @PathVariable UUID simulationId,
        @Parameter(description = "초대할 협업자 정보") @RequestBody CollaboratorRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            service.inviteCollaborator(simulationId, userId, request)));
    }

    @Operation(summary = "협업자 목록 조회", description = "시뮬레이션의 협업자 목록을 조회합니다.")
    @GetMapping("/{simulationId}/collaborators")
    public ResponseEntity<ApiResponse<List<CollaboratorResponse>>> getCollaborators(
        @Parameter(description = "협업자를 조회할 시뮬레이션 ID") @PathVariable UUID simulationId,
        @Parameter(hidden = true) /* @AuthenticationPrincipal */ UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            service.getCollaborators(userId, simulationId)));
    }

    @Operation(summary = "협업자 제거", description = "시뮬레이션에서 협업자를 제거합니다. 소유자만 제거할 수 있습니다.")
    @DeleteMapping("/{simulationId}/collaborators/{collaboratorId}")
    public ResponseEntity<ApiResponse<Void>> removeCollaborator(
        @Parameter(description = "협업자를 제거할 시뮬레이션 ID") @PathVariable UUID simulationId,
        @Parameter(description = "제거할 협업자 ID") @PathVariable UUID collaboratorId,
        @Parameter(hidden = true) /* @AuthenticationPrincipal */ UUID userId
    ) {
        service.removeCollaborator(userId, simulationId, collaboratorId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
