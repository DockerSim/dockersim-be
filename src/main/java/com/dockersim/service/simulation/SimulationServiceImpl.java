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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SimulationServiceImpl implements SimulationService {

    private static final int MAX_COLLABORATORS = 6;

    private final UserFinder userFinder;
    private final SimulationFinder simulationFinder; // SimulationFinder 주입
    private final SimulationRepository simulationRepository;

    @Override
    public SimulationResponse createSimulation(String ownerId, SimulationRequest request) {
        User owner = userFinder.findUserByPublicId(ownerId);
        validateSimulationTitle(request.getTitle(), ownerId);
        SimulationShareState shareState = validateShareState(request.getShareState());

        Simulation simulation = Simulation.from(request, shareState, owner);
        return SimulationResponse.from(simulationRepository.save(simulation));
    }

    @Override
    @Transactional(readOnly = true)
    public SimulationResponse getSimulation(String userId, String simulationPublicId) {
        log.info("SimulationServiceImpl: Attempting to get simulation by publicId: {}", simulationPublicId); // 로그 추가
        Simulation simulation = simulationFinder.findByPublicId(simulationPublicId);
        log.info("SimulationServiceImpl: Found simulation with publicId: {}", simulation.getPublicId()); // 로그 추가

        if (simulation.getShareState() != SimulationShareState.READ) {
            User user = userFinder.findUserByPublicId(userId);
            validateSimulationAccess(simulation, user);
        }

        return SimulationResponse.from(simulation);
    }

    @Override
    public SimulationResponse updateSimulation(String ownerId, String simulationPublicId,
        SimulationRequest request) {
        User owner = userFinder.findUserByPublicId(ownerId);
        Simulation simulation = simulationFinder.findByPublicId(simulationPublicId);

        validateOwnership(simulation, owner);
        validateSimulationTitle(request.getTitle(), ownerId, simulationPublicId);
        SimulationShareState newShareState = validateShareState(request.getShareState());

        if (simulation.getShareState() == SimulationShareState.WRITE &&
            newShareState != SimulationShareState.WRITE) {
            simulation.removeAllCollaborators();
        }

        simulation.updateTitle(request.getTitle());
        simulation.updateShareState(newShareState);

        return SimulationResponse.from(simulationRepository.save(simulation));
    }

    @Override
    public void deleteSimulation(String ownerId, String simulationPublicId) {
        User owner = userFinder.findUserByPublicId(ownerId);
        Simulation simulation = simulationFinder.findByPublicId(simulationPublicId);

        validateOwnership(simulation, owner);

        simulationRepository.delete(simulation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulationResponse> getMySimulations(String userPublicId) {
        User owner = userFinder.findUserByPublicId(userPublicId);
        List<Simulation> simulations = simulationRepository.findAllByOwner(owner);
        log.info("Found {} simulations for userPublicId: {}", simulations.size(), userPublicId);
        return simulations.stream()
                .map(SimulationResponse::from)
                .toList();
    }

    @Override
    public CollaboratorResponse inviteCollaborator(String simulationId, String ownerId,
        CollaboratorRequest request) {
        User owner = userFinder.findUserByPublicId(ownerId);
        Simulation simulation = simulationFinder.findSimulationWithCollaborators(simulationId);

        validateOwnership(simulation, owner);
        validateCollaboratorInvitation(simulation);

        User invitee = userFinder.findUserByEmail(request.getEmail());
        validateInvitee(simulation, invitee);

        simulation.addCollaborator(invitee, owner);
        simulationRepository.save(simulation);

        return CollaboratorResponse.from(simulation.findCollaborator(invitee));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollaboratorResponse> getCollaborators(String userId, String simulationId) {
        User user = userFinder.findUserByPublicId(userId);
        Simulation simulation = simulationFinder.findSimulationWithCollaborators(simulationId);

        validateSimulationAccess(simulation, user);

        return simulation.getCollaborators().stream()
            .map(CollaboratorResponse::from)
            .toList();
    }

    @Override
    public void removeCollaborator(String ownerId, String simulationId, String collaboratorId) {
        User owner = userFinder.findUserByPublicId(ownerId);
        User collaborator = userFinder.findUserByPublicId(collaboratorId);
        Simulation simulation = simulationFinder.findSimulationWithCollaborators(simulationId);

        validateOwnership(simulation, owner);
        validateCollaboratorRemoval(simulation, collaborator);

        simulation.removeCollaborator(collaborator);
        simulationRepository.save(simulation);
    }

    // findSimulationByString, findSimulationWithCollaborators 메서드 제거

    private void validateSimulationTitle(String title, String ownerId, String currentSimulationId) {
        if (currentSimulationId != null) {
            if (simulationRepository.existsByTitleAndOwnerIdAndNotId(title, ownerId,
                currentSimulationId)) {
                throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_DUPLICATE, title);
            }
        } else {
            if (simulationRepository.existsByTitleAndOwnerId(title, ownerId)) {
                throw new BusinessException(SimulationErrorCode.SIMULATION_TITLE_DUPLICATE, title);
            }
        }
    }

    private void validateSimulationTitle(String title, String ownerId) {
        validateSimulationTitle(title, ownerId, null);
    }

    private SimulationShareState validateShareState(String shareStateStr) {
        log.info("Validating share state: {}", shareStateStr);
        if (shareStateStr == null) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_INVALID_SHARE_STATE, "null");
        }
        try {
            return SimulationShareState.valueOf(shareStateStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_INVALID_SHARE_STATE,
                shareStateStr);
        }
    }

    private void validateOwnership(Simulation simulation, User user) {
        if (!simulation.isOwner(user)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ONLY_OWNER_CAN_MANAGE);
        }
    }

    private void validateSimulationAccess(Simulation simulation, User user) {
        if (!simulation.hasWriteAccess(user)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_ACCESS_DENIED);
        }
    }

    private void validateCollaboratorInvitation(Simulation simulation) {
        if (simulation.getShareState() != SimulationShareState.WRITE) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_NOT_WRITABLE);
        }

        long collaboratorCount = simulationRepository.countCollaborators(
            simulation.getPublicId());
        if (collaboratorCount >= MAX_COLLABORATORS) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_MAX_COLLABORATORS_REACHED,
                MAX_COLLABORATORS);
        }
    }

    private void validateInvitee(Simulation simulation, User invitee) {
        if (simulation.isOwner(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_OWNER_CANNOT_BE_COLLABORATOR,
                invitee.getPublicId());
        }
        if (simulation.isCollaborator(invitee)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_ALREADY_EXISTS,
                invitee.getEmail());
        }
    }

    private void validateCollaboratorRemoval(Simulation simulation, User collaborator) {
        if (!simulation.isCollaborator(collaborator)) {
            throw new BusinessException(SimulationErrorCode.SIMULATION_COLLABORATOR_NOT_FOUND,
                collaborator.getPublicId());
        }
    }
}
