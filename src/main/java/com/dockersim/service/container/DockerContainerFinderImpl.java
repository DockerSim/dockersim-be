package com.dockersim.service.container;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerContainerErrorCode;
import com.dockersim.repository.DockerContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DockerContainerFinderImpl implements DockerContainerFinder {
    private final DockerContainerRepository repo;

    @Override
    public DockerContainer findByIdentifier(Simulation simulation, String nameOrHexId) {
        return repo.findByNameOrHexId(simulation, nameOrHexId).orElseThrow(
                () -> new BusinessException(DockerContainerErrorCode.NOT_FOUND_CONTAINER, nameOrHexId)
        );
    }

    @Override
    public DockerContainer findBySimulationAndIdentifierAndStatus(Simulation simulation, String nameOrHexId, ContainerStatus status) {
        return repo.findBySimulationAndIdentifierAndStatus(simulation, nameOrHexId, status)
                .orElseThrow(() -> new BusinessException(DockerContainerErrorCode.NOT_FOUND_CONTAINER, nameOrHexId));
    }

    @Override
    public List<DockerContainer> findBySimulation(Simulation simulation, boolean all) {
        if (all) {
            return repo.findAllBySimulation(simulation);
        }
        return repo.findAllBySimulationAndStatus(simulation, ContainerStatus.RUNNING);
    }
}
