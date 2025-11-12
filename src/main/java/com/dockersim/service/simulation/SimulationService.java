package com.dockersim.service.simulation;

import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import java.util.List;

public interface SimulationService {

    /**
     * 시뮬레이션 생성
     *
     * @param ownerId 소유자 ID
     * @param request 생성할 시뮬레이션 정보
     * @return 생성된 시뮬레이션 정보
     */
    SimulationResponse createSimulation(String ownerId, SimulationRequest request);

    /**
     * 시뮬레이션 조회 SimulationShareState가 READ이면 별도 권한 검증 없이 조회 가능(수정 불가)
     *
     * @param ownerId      소유자 ID
     * @param simulationId 시뮬레이션 ID
     * @return 시뮬레이션 정보
     */
    SimulationResponse getSimulation(String ownerId, String simulationId);

    /**
     * 시뮬레이션 정보 수정(제목 및 공유 상태 변경) 소유자만 수정 가능. SimulationShareState가 WRITE -> READ / PRIVATE로 변경될 경우
     * 모든 협업자 삭제
     *
     * @param simulationId 수정할 시뮬레이션 ID
     * @param ownerId      소유자 ID
     * @param request      수정할 내용(제목 및 공유 상태)
     * @return 수정된 시뮬레이션 정보
     */
    SimulationResponse updateSimulation(String ownerId, String simulationId,
        SimulationRequest request);

    /**
     * 시뮬레이션 삭제
     *
     * @param ownerId      소유자 ID
     * @param simulationId 삭제할 시뮬레이션 ID
     */
    void deleteSimulation(String ownerId, String simulationId);

    /**
     * 현재 로그인한 사용자의 모든 시뮬레이션 목록을 조회합니다.
     * @param userPublicId 사용자 공개 ID
     * @return 사용자의 시뮬레이션 목록
     */
    List<SimulationResponse> getMySimulations(String userPublicId); // 새로운 메서드 추가

    /**
     * 협업자 초대 SimulationShareState가 WRITE일 때만 초대 가능
     *
     * @param simulationId 시뮬레이션 ID
     * @param ownerId      소유자 ID
     * @param request      초대할 협업자 정보 (이메일)
     * @return 초대된 협업자 정보
     */
    CollaboratorResponse inviteCollaborator(String simulationId, String ownerId,
        CollaboratorRequest request);

    /**
     * 협업자 목록 조회 각 협업자가 협업중인 시뮬레이션의 협업자 목록을 조회
     *
     * @param simulationId 시뮬레이션 ID
     * @param userId       요청자 ID (권한 확인용)
     */
    List<CollaboratorResponse> getCollaborators(String userId, String simulationId);

    /**
     * 협업자 제거 소유자만 협업자 제거 가능
     *
     * @param simulationId       시뮬레이션 ID
     * @param collaboratorUserId 제거할 협업자 ID
     * @param ownerId            요청자 ID (소유자 확인용)
     */
    void removeCollaborator(String ownerId, String simulationId, String collaboratorUserId);

}
