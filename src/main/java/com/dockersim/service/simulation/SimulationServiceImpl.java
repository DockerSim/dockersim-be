package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;
import com.dockersim.domain.SimulationCollaborator;
import com.dockersim.domain.SimulationShareState;
import com.dockersim.domain.User;
import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.request.UpdatePermissionRequest;
import com.dockersim.dto.request.UpdateShareStateRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.SimulationRepository;
import com.dockersim.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SimulationResponse createSimulation(Long ownerId, SimulationRequest request) {
        log.info("시뮬레이션 생성 요청: title={}", request.getTitle());

        SimulationShareState shareState = validateShareState(request.getShareState());

        User owner = userRepository.findById(ownerId)
            .orElseThrow(
                () -> new BusinessException(UserErrorCode.USER_NOT_FOUND, ownerId));

        if (simulationRepository.existsByTitleAndUser(request.getTitle(), owner)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_DUPLICATE,
                request.getTitle());
        }

        Simulation saved = simulationRepository.save(
            Simulation.fromSimulationRequest(request, shareState, owner)
        );
        log.info("시뮬레이션 생성 완료: id={}, title={}", saved.getId(), saved.getTitle());
        return toSimulationResponse(saved);
    }

    @Override
    public SimulationResponse getSimulation(Long ownerId, Long simulationId) {
        log.info("시뮬레이션 조회 요청: id={}", simulationId);

        Simulation simulation = simulationRepository.findById(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
        return toSimulationResponse(simulation);
    }

    @Override
    @Transactional
    public SimulationResponse updateShareState(Long simulationId, Long ownerId,
        UpdateShareStateRequest request) {
        log.info("공유 상태 변경 요청: simulationId={}, ownerId={}, shareState={}",
            simulationId, ownerId, request.getShareState());

        SimulationShareState shareState = validateShareState(request.getShareState());
        Simulation simulation = getSimulationWithOwnerValidation(simulationId, ownerId);

        simulation.updateShareState(shareState);
        Simulation updated = simulationRepository.save(simulation);
        log.info("공유 상태 변경 완료: simulationId={}, shareState={}", simulationId, shareState);
        return toSimulationResponse(updated);
    }

    @Override
    @Transactional
    public CollaboratorResponse inviteCollaborator(Long simulationId, Long ownerId,
        CollaboratorRequest request) {
        log.info("협업자 초대 요청: simulationId={}, ownerId={}, email={}",
            simulationId, ownerId, request.getEmail());

        Simulation simulation = getSimulationWithOwnerValidation(simulationId, ownerId);

        User invitee = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_EMAIL_NOT_FOUND,
                request.getEmail()));
        validateCollaboratorInvitation(simulation, invitee);

        simulation.addCollaborator(invitee, simulation.getUser());
        simulationRepository.save(simulation);
        SimulationCollaborator collab = simulation.findCollaborator(invitee);
        log.info("협업자 초대 완료: simulationId={}, userId={}", simulationId, invitee.getId());
        return CollaboratorResponse.from(collab);
    }


    @Override
    public List<CollaboratorResponse> getCollaborators(Long simulationId, Long userId) {
        log.info("협업자 목록 조회 요청: simulationId={}, userId={}", simulationId, userId);

        Simulation simulation = simulationRepository.findById(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND,
                userId));

        if (!simulation.hasAccess(user)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ACCESS_DENIED,
                simulationId, userId);
        }

        return simulation.getCollaborators().stream()
            .map(CollaboratorResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeCollaborator(Long simulationId, Long collaboratorUserId, Long requesterId) {
        log.info("협업자 제거 요청: simulationId={}, collaboratorUserId={}, requesterId={}",
            simulationId, collaboratorUserId, requesterId);

        Simulation simulation = simulationRepository.findById(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND,
                requesterId));
        User collaborator = userRepository.findById(collaboratorUserId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND,
                collaboratorUserId));

        validateCollaboratorRemoval(simulation, collaborator, requester);

        simulation.removeCollaborator(collaborator);
        simulationRepository.save(simulation);
        log.info("협업자 제거 완료: simulationId={}, userId={}", simulationId, collaboratorUserId);
    }

    @Override
    @Transactional
    public CollaboratorResponse updateCollaboratorPermission(Long simulationId,
        Long collaboratorUserId, Long ownerId, UpdatePermissionRequest request) {
        log.info("협업자 권한 변경 요청: simulationId={}, collaboratorUserId={}, ownerId={}, permission={}",
            simulationId, collaboratorUserId, ownerId, request.getPermission());

        Simulation sim = getSimulationWithOwnerValidation(simulationId, ownerId);
        User collaborator = userRepository.findById(collaboratorUserId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                collaboratorUserId));

        if (!sim.isCollaborator(collaborator)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_NOT_FOUND,
                collaboratorUserId);
        }

        simulationRepository.save(sim);
        return CollaboratorResponse.from(sim.findCollaborator(collaborator));
    }

    @Override
    @Transactional
    public SimulationResponse updateSimulation(Long simulationId, Long ownerId,
        SimulationRequest request) {
        log.info("시뮬레이션 수정 요청: simulationId={}, ownerId={}, title={}",
            simulationId, ownerId, request.getTitle());

        Simulation simulation = getSimulationWithOwnerValidation(simulationId, ownerId);

        // 제목 중복 확인 (자신의 것은 제외)
        if (!simulation.getTitle().equals(request.getTitle()) &&
            simulationRepository.existsByTitleAndUser(request.getTitle(), simulation.getUser())) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_DUPLICATE,
                request.getTitle());
        }

        simulation.updateTitle(request.getTitle());
        if (request.getShareState() != null) {
            SimulationShareState shareState = validateShareState(request.getShareState());
            simulation.updateShareState(shareState);
        }

        Simulation updated = simulationRepository.save(simulation);
        log.info("시뮬레이션 수정 완료: id={}, title={}", updated.getId(), updated.getTitle());
        return toSimulationResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSimulation(Long simulationId, Long ownerId) {
        log.info("시뮬레이션 삭제 요청: simulationId={}, ownerId={}", simulationId, ownerId);

        Simulation simulation = getSimulationWithOwnerValidation(simulationId, ownerId);
        simulationRepository.delete(simulation);

        log.info("시뮬레이션 삭제 완료: id={}", simulationId);
    }

    // Helper methods
    private SimulationShareState validateShareState(String shareStateStr) {
        try {
            return SimulationShareState.valueOf(shareStateStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_INVALID_SHARE_STATE,
                shareStateStr);
        }
    }

    private Simulation getSimulationWithOwnerValidation(Long simulationId, Long ownerId) {
        Simulation simulation = simulationRepository.findById(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND,
                ownerId));

        if (!simulation.isOwner(owner)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE,
                ownerId, simulationId);
        }

        return simulation;
    }

    private void validateCollaboratorInvitation(Simulation sim, User invitee) {
        if (sim.isOwner(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_OWNER_CANNOT_BE_COLLABORATOR,
                invitee.getId());
        }
        if (sim.isCollaborator(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_ALREADY_EXISTS,
                invitee.getEmail());
        }
    }

    private void validateCollaboratorRemoval(Simulation simulation, User collaborator,
        User requester) {
        if (!simulation.isCollaborator(collaborator)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_NOT_FOUND,
                collaborator.getId());
        }

        boolean isOwner = simulation.isOwner(requester);
        boolean isSelf = requester.getId().equals(collaborator.getId());
        if (!isOwner && !isSelf) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE,
                requester.getId(), simulation.getId());
        }
    }

    private SimulationResponse toSimulationResponse(Simulation simulation) {
        return SimulationResponse.from(simulation);
    }
}
