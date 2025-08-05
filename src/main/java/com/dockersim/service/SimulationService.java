package com.dockersim.service;

import com.dockersim.domain.*;
import com.dockersim.dto.request.*;
import com.dockersim.dto.response.*;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.repository.SimulationRepository;
import com.dockersim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;

    /**
     * 시뮬레이션 생성
     */
    @Transactional
    public SimulationResponse createSimulation(CreateSimulationRequest request) {
        log.info("시뮬레이션 생성 요청: title={}, userId={}", request.getTitle(), request.getUserId());

        // 1. 공유 상태 유효성 검증
        ShareState shareState = validateShareState(request.getShareState());

        // 2. 사용자 존재 확인
        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, request.getUserId()));

        // 3. 제목 중복 확인
        if (simulationRepository.existsByTitleAndOwner(request.getTitle(), owner)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_DUPLICATE, request.getTitle());
        }

        // 4. 시뮬레이션 생성
        Simulation saved = simulationRepository.save(
                new Simulation(request.getTitle(), shareState, owner)
        );
        log.info("시뮬레이션 생성 완료: id={}, title={}", saved.getId(), saved.getTitle());
        return toSimulationResponse(saved);
    }

    public SimulationResponse getSimulation(Long id) {
        log.info("시뮬레이션 조회 요청: id={}", id);

        Simulation sim = simulationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, id));
        return toSimulationResponse(sim);
    }

    @Transactional
    public SimulationResponse updateShareState(Long simulationId, Long ownerId, UpdateShareStateRequest request) {
        log.info("공유 상태 변경 요청: simulationId={}, ownerId={}, shareState={}",
                simulationId, ownerId, request.getShareState());

        // 1. 공유 상태 유효성 검증
        ShareState shareState = validateShareState(request.getShareState());

        // 2. 시뮬레이션 & 소유자 검증
        Simulation sim = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, simulationId));
        if (!sim.isOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, ownerId)))) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE, ownerId, simulationId);
        }

        // 3. 상태 변경 & 저장
        sim.updateShareState(shareState);
        Simulation updated = simulationRepository.save(sim);
        log.info("공유 상태 변경 완료: simulationId={}, shareState={}", simulationId, shareState);
        return toSimulationResponse(updated);
    }

    @Transactional
    public CollaboratorResponse inviteCollaborator(Long simulationId, Long ownerId, InviteCollaboratorRequest request) {
        log.info("협업자 초대 요청: simulationId={}, ownerId={}, email={}",
                simulationId, ownerId, request.getEmail());

        // 권한 검증
        Permission perm = validatePermission(request.getPermission());

        // 시뮬레이션 검증
        Simulation sim = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, simulationId));

        // 소유자 검증
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, ownerId));
        if (!sim.isOwner(owner)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE, ownerId, simulationId);
        }

        // 대상 사용자 검증
        User invitee = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, request.getEmail()));

        // 자기자신 초대 금지
        if (sim.isOwner(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_OWNER_CANNOT_BE_COLLABORATOR, invitee.getId());
        }
        // 중복 초대 금지
        if (sim.isCollaborator(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_ALREADY_EXISTS, request.getEmail());
        }

        sim.addCollaborator(invitee, perm, owner);
        simulationRepository.save(sim);
        SimulationCollaborator collab = sim.findCollaborator(invitee);
        log.info("협업자 초대 완료: simulationId={}, userId={}", simulationId, invitee.getId());
        return CollaboratorResponse.from(collab);
    }

    public List<CollaboratorResponse> getCollaborators(Long simulationId, Long userId) {
        log.info("협업자 목록 조회 요청: simulationId={}, userId={}", simulationId, userId);

        Simulation sim = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, simulationId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, userId));

        if (!sim.hasAccess(user)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ACCESS_DENIED, simulationId, userId);
        }

        return sim.getCollaborators().stream()
                .map(CollaboratorResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeCollaborator(Long simulationId, Long collaboratorUserId, Long requesterId) {
        log.info("협업자 제거 요청: simulationId={}, collaboratorUserId={}, requesterId={}",
                simulationId, collaboratorUserId, requesterId);

        Simulation sim = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, simulationId));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, requesterId));

        if (!sim.isCollaborator(userRepository.findById(collaboratorUserId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, collaboratorUserId)))) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_NOT_FOUND, collaboratorUserId);
        }

        boolean isOwner = sim.isOwner(requester);
        boolean isSelf  = requesterId.equals(collaboratorUserId);
        if (!isOwner && !isSelf) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE, requesterId, simulationId);
        }

        sim.removeCollaborator(userRepository.findById(collaboratorUserId).get());
        simulationRepository.save(sim);
        log.info("협업자 제거 완료: simulationId={}, userId={}", simulationId, collaboratorUserId);
    }

    @Transactional
    public CollaboratorResponse updateCollaboratorPermission(Long simulationId, Long collaboratorUserId,
                                                             Long ownerId, UpdatePermissionRequest request) {
        log.info("협업자 권한 변경 요청: simulationId={}, collaboratorUserId={}, ownerId={}, permission={}",
                simulationId, collaboratorUserId, ownerId, request.getPermission());

        Permission perm = validatePermission(request.getPermission());

        Simulation sim = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, simulationId));
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, ownerId));
        if (!sim.isOwner(owner)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE, ownerId, simulationId);
        }

        User collaborator = userRepository.findById(collaboratorUserId)
                .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND, collaboratorUserId));
        if (!sim.isCollaborator(collaborator)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_NOT_FOUND, collaboratorUserId);
        }

        sim.findCollaborator(collaborator).updatePermission(perm);
        simulationRepository.save(sim);
        log.info("협업자 권한 변경 완료: simulationId={}, userId={}, permission={}",
                simulationId, collaboratorUserId, perm);
        return CollaboratorResponse.from(sim.findCollaborator(collaborator));
    }

    // 유효성 헬퍼들
    private ShareState validateShareState(String shareStateStr) {
        try {
            return ShareState.valueOf(shareStateStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_INVALID_SHARE_STATE, shareStateStr);
        }
    }

    private Permission validatePermission(String permissionStr) {
        try {
            return Permission.valueOf(permissionStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_INVALID_PERMISSION, permissionStr);
        }
    }

    private SimulationResponse toSimulationResponse(Simulation sim) {
        return new SimulationResponse(
                sim.getId(),
                sim.getTitle(),
                sim.getShareState().name(),
                sim.getOwner().getId(),
                sim.getCreatedAt(),
                sim.getUpdatedAt()
        );
    }
}