package com.dockersim.service.simulation;

import com.dockersim.domain.Simulation;
import com.dockersim.domain.SimulationShareState;
import com.dockersim.domain.User;
import com.dockersim.dto.request.CollaboratorRequest;
import com.dockersim.dto.request.SimulationRequest;
import com.dockersim.dto.response.CollaboratorResponse;
import com.dockersim.dto.response.SimulationResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.SimulationErrorCode;
import com.dockersim.repository.SimulationRepository;
import com.dockersim.service.user.UserFinder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimulationServiceImpl implements SimulationService {

    private static final int MAX_COLLABORATORS = 6;

    private final UserFinder userFinder;
    private final SimulationRepository simulationRepository;

    @Override
    @Transactional
    public SimulationResponse createSimulation(UUID ownerId, SimulationRequest request) {
        User owner = userFinder.findUserByUUID(ownerId);
        validateSimulationTitle(request.getTitle(), ownerId);
        SimulationShareState shareState = validateShareState(request.getShareState());

        Simulation simulation = Simulation.from(request, shareState, owner);
        return SimulationResponse.from(simulationRepository.save(simulation));
    }

    @Override
    public SimulationResponse getSimulation(UUID userId, UUID simulationId) {
        Simulation simulation = findSimulationByUUID(simulationId);

        if (simulation.getShareState() != SimulationShareState.READ) {
            User user = userFinder.findUserByUUID(userId);
            validateSimulationAccess(simulation, user);
        }

        return SimulationResponse.from(simulation);
    }

    @Override
    @Transactional
    public SimulationResponse updateSimulation(UUID ownerId, UUID simulationId,
        SimulationRequest request) {
        User owner = userFinder.findUserByUUID(ownerId);
        Simulation simulation = findSimulationByUUID(simulationId);

        validateOwnership(simulation, owner);
        validateSimulationTitle(request.getTitle(), ownerId, simulationId);
        SimulationShareState newShareState = validateShareState(request.getShareState());

        // WRITE에서 다른 상태로 변경되는 경우 협업자 모두 제거
        if (simulation.getShareState() == SimulationShareState.WRITE &&
            newShareState != SimulationShareState.WRITE) {
            simulation.removeAllCollaborators();
        }

        simulation.updateTitle(request.getTitle());
        simulation.updateShareState(newShareState);

        return SimulationResponse.from(simulationRepository.save(simulation));
    }

    @Override
    @Transactional
    public void deleteSimulation(UUID ownerId, UUID simulationId) {
        User owner = userFinder.findUserByUUID(ownerId);
        Simulation simulation = findSimulationByUUID(simulationId);

        validateOwnership(simulation, owner);

        simulationRepository.delete(simulation);
    }

    @Override
    @Transactional
    public CollaboratorResponse inviteCollaborator(UUID simulationId, UUID ownerId,
        CollaboratorRequest request) {
        User owner = userFinder.findUserByUUID(ownerId);
        Simulation simulation = findSimulationWithCollaborators(simulationId);

        validateOwnership(simulation, owner);
        validateCollaboratorInvitation(simulation);

        User invitee = userFinder.findUserByEmail(request.getEmail());
        validateInvitee(simulation, invitee);

        simulation.addCollaborator(invitee, owner);
        simulationRepository.save(simulation);

        return CollaboratorResponse.from(simulation.findCollaborator(invitee));
    }

    @Override
    public List<CollaboratorResponse> getCollaborators(UUID userId, UUID simulationId) {
        User user = userFinder.findUserByUUID(userId);
        Simulation simulation = findSimulationWithCollaborators(simulationId);

        validateSimulationAccess(simulation, user);

        return simulation.getCollaborators().stream()
            .map(CollaboratorResponse::from)
            .toList();
    }

    @Override
    @Transactional
    public void removeCollaborator(UUID ownerId, UUID simulationId, UUID collaboratorId) {
        User owner = userFinder.findUserByUUID(ownerId);
        User collaborator = userFinder.findUserByUUID(collaboratorId);
        Simulation simulation = findSimulationWithCollaborators(simulationId);

        validateOwnership(simulation, owner);
        validateCollaboratorRemoval(simulation, collaborator);

        simulation.removeCollaborator(collaborator);
        simulationRepository.save(simulation);
    }


    @Override
    public Simulation findSimulationByUUID(UUID simulationId) {
        return simulationRepository.findBySimulationId(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
    }

    @Override
    public Simulation findSimulationWithCollaborators(UUID simulationId) {
        return simulationRepository.findBySimulationIdWithCollaborators(simulationId)
            .orElseThrow(() -> new BusinessException(SimulationErrorCode.SIMULATION_NOT_FOUND,
                simulationId));
    }

    private void validateSimulationTitle(String title, UUID ownerId, UUID currentSimulationId) {
        if (currentSimulationId != null) {
            // 수정 시에는 자기 자신을 제외하고 중복 검사
            if (simulationRepository.existsByTitleAndOwnerIdAndNotId(title, ownerId,
                currentSimulationId)) {
                throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_DUPLICATE, title);
            }
        } else {
            // 생성 시에는 단순 중복 검사
            if (simulationRepository.existsByTitleAndOwnerId(title, ownerId)) {
                throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_DUPLICATE, title);
            }
        }
    }

    // 기존 메서드는 생성 시에만 사용
    private void validateSimulationTitle(String title, UUID ownerId) {
        validateSimulationTitle(title, ownerId, null);
    }

    private SimulationShareState validateShareState(String shareStateStr) {
        try {
            return SimulationShareState.valueOf(shareStateStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_INVALID_SHARE_STATE,
                shareStateStr);
        }
    }

    private void validateOwnership(Simulation simulation, User user) {
        if (!simulation.isOwner(user)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE,
                user.getUserId(), simulation.getSimulationId());
        }
    }

    private void validateSimulationAccess(Simulation simulation, User user) {
        if (!simulation.hasWriteAccess(user)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ACCESS_DENIED,
                simulation.getSimulationId(), user.getUserId());
        }
    }

    private void validateCollaboratorInvitation(Simulation simulation) {
        if (simulation.getShareState() != SimulationShareState.WRITE) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_NOT_WRITABLE,
                simulation.getSimulationId());
        }

        long collaboratorCount = simulationRepository.countCollaborators(
            simulation.getSimulationId());
        if (collaboratorCount >= MAX_COLLABORATORS) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_MAX_COLLABORATORS_REACHED,
                MAX_COLLABORATORS);
        }
    }

    private void validateInvitee(Simulation simulation, User invitee) {
        if (simulation.isOwner(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_OWNER_CANNOT_BE_COLLABORATOR,
                invitee.getUserId());
        }
        if (simulation.isCollaborator(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_ALREADY_EXISTS,
                invitee.getEmail());
        }
    }

    private void validateCollaboratorRemoval(Simulation simulation, User collaborator) {
        if (!simulation.isCollaborator(collaborator)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_NOT_FOUND,
                collaborator.getUserId());
        }
    }
}
