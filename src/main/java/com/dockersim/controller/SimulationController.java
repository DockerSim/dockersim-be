package com.dockersim.controller;

import com.dockersim.dto.request.*;
import com.dockersim.dto.response.*;
import com.dockersim.service.SimulationService;
import com.dockersim.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class SimulationController {

        private final SimulationService simulationService;

        @PostMapping
        public ResponseEntity<ApiResponse<SimulationResponse>> createSimulation(
                        @RequestBody CreateSimulationRequest request) {
                log.info("시뮬레이션 생성 요청: {}", request.getTitle());

                SimulationResponse simulationResponse = simulationService.createSimulation(request);

                ApiResponse<SimulationResponse> response = ApiResponse.ok(simulationResponse);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<SimulationResponse>> getSimulation(@PathVariable Long id) {
                log.info("시뮬레이션 조회 요청: id={}", id);

                SimulationResponse simulationResponse = simulationService.getSimulation(id);

                ApiResponse<SimulationResponse> response = ApiResponse.ok(simulationResponse);

                return ResponseEntity.ok(response);
        }

        /**
         * 시뮬레이션 공유 상태 변경 API
         */
        @PatchMapping("/{id}/shareState")
        public ResponseEntity<ApiResponse<SimulationResponse>> updateShareState(
                        @PathVariable Long id,
                        @RequestParam Long ownerId,
                        @RequestBody UpdateShareStateRequest request) {
                log.info("공유 상태 변경 요청: simulationId={}, ownerId={}", id, ownerId);

                SimulationResponse simulationResponse = simulationService.updateShareState(id, ownerId, request);

                ApiResponse<SimulationResponse> response = ApiResponse.ok(simulationResponse);

                return ResponseEntity.ok(response);
        }

        /**
         * 협업자 초대 API
         */
        @PostMapping("/{id}/collaborators")
        public ResponseEntity<ApiResponse<CollaboratorResponse>> inviteCollaborator(
                        @PathVariable Long id,
                        @RequestParam Long ownerId,
                        @RequestBody InviteCollaboratorRequest request) {
                log.info("협업자 초대 요청: simulationId={}, ownerId={}, email={}", id, ownerId, request.getEmail());

                CollaboratorResponse collaboratorResponse = simulationService.inviteCollaborator(id, ownerId, request);

                ApiResponse<CollaboratorResponse> response = ApiResponse.ok(collaboratorResponse);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * 협업자 목록 조회 API
         */
        @GetMapping("/{id}/collaborators")
        public ResponseEntity<ApiResponse<List<CollaboratorResponse>>> getCollaborators(
                        @PathVariable Long id,
                        @RequestParam Long userId) {
                log.info("협업자 목록 조회 요청: simulationId={}, userId={}", id, userId);

                List<CollaboratorResponse> collaborators = simulationService.getCollaborators(id, userId);

                ApiResponse<List<CollaboratorResponse>> response = ApiResponse.ok(collaborators);

                return ResponseEntity.ok(response);
        }

        /**
         * 협업자 제거 API
         */
        @DeleteMapping("/{id}/collaborators/{userId}")
        public ResponseEntity<ApiResponse<Void>> removeCollaborator(
                        @PathVariable Long id,
                        @PathVariable Long userId,
                        @RequestParam Long requesterId) {
                log.info("협업자 제거 요청: simulationId={}, collaboratorUserId={}, requesterId={}", id, userId, requesterId);

                simulationService.removeCollaborator(id, userId, requesterId);

                ApiResponse<Void> response = ApiResponse.ok(null);

                return ResponseEntity.ok(response);
        }

        /**
         * 협업자 권한 변경 API
         */
        @PatchMapping("/{id}/collaborators/{userId}/permission")
        public ResponseEntity<ApiResponse<CollaboratorResponse>> updateCollaboratorPermission(
                        @PathVariable Long id,
                        @PathVariable Long userId,
                        @RequestParam Long ownerId,
                        @RequestBody UpdatePermissionRequest request) {
                log.info("협업자 권한 변경 요청: simulationId={}, collaboratorUserId={}, ownerId={}", id, userId, ownerId);

                CollaboratorResponse collaboratorResponse = simulationService.updateCollaboratorPermission(id, userId,
                                ownerId, request);

                ApiResponse<CollaboratorResponse> response = ApiResponse.ok(collaboratorResponse);

                return ResponseEntity.ok(response);
        }
}