package com.dockersim.service.simulation;

import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.request.UpdatePermissionRequest;
import com.dockersim.dto.request.UpdateShareStateRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import java.util.List;

public interface SimulationService {

    /**
     * 시뮬레이션 생성
     */
    SimulationResponse createSimulation(Long ownerId, SimulationRequest request);

    /**
     * 시뮬레이션 조회
     */
    SimulationResponse getSimulation(Long ownerId, Long simulationId);

    /**
     * 시뮬레이션 수정
     *
     * @param simulationId 수정할 시뮬레이션 ID
     * @param ownerId      소유자 ID
     * @param request      수정할 내용
     * @return 수정된 시뮬레이션 정보
     */
    SimulationResponse updateSimulation(Long simulationId, Long ownerId, SimulationRequest request);

    /**
     * 시뮬레이션 삭제
     *
     * @param simulationId 삭제할 시뮬레이션 ID
     * @param ownerId      소유자 ID
     */
    void deleteSimulation(Long simulationId, Long ownerId);

    /**
     * 공유 상태 변경
     */
    SimulationResponse updateShareState(Long simulationId, Long ownerId,
        UpdateShareStateRequest request);

    /**
     * 협업자 초대
     */
    CollaboratorResponse inviteCollaborator(Long simulationId, Long ownerId,
        CollaboratorRequest request);

    /**
     * 협업자 목록 조회
     */
    List<CollaboratorResponse> getCollaborators(Long simulationId, Long userId);

    /**
     * 협업자 제거
     */
    void removeCollaborator(Long simulationId, Long collaboratorUserId, Long requesterId);

    /**
     * 협업자 권한 변경
     */
    CollaboratorResponse updateCollaboratorPermission(Long simulationId, Long collaboratorUserId,
        Long ownerId, UpdatePermissionRequest request);


}
