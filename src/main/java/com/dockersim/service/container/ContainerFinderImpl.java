package com.dockersim.service.container;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerContainerErrorCode;
import com.dockersim.repository.DockerContainerRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContainerFinderImpl implements ContainerFinder {

    private final DockerContainerRepository repo;

    @Override
    public boolean existsByImageIdAndStatus(String imageId, ContainerStatus status) {
        return repo.existsByImageIdAndStatus(imageId, status);
    }

    @Override
    public List<DockerContainer> findAllBySimulation(Simulation simulation) {
        return repo.findAllBySimulation(simulation);
    }

    @Override
    public Optional<DockerContainer> findByName(String name, Simulation simulation) {
        return repo.findByNameAndSimulation(name, simulation);
    }

    @Override
    public Optional<DockerContainer> findByContainerId(String containerId, Simulation simulation) {
        return repo.findByContainerIdAndSimulation(containerId, simulation);
    }

    @Override
    public DockerContainer findByNameOrIdAndStatusAndSimulationId(
        String nameOrId, ContainerStatus status, String simulationId
    ) {
        return repo.findByNameAndStatusAndSimulationId(
            nameOrId,
            status,
            simulationId
        ).or(
            () -> repo.findByContainerIdAndStatusAndSimulationId(
                nameOrId,
                status,
                simulationId
            )
        ).orElseThrow(
            () -> new BusinessException(DockerContainerErrorCode.CONTAINER_NOT_RUNNING, nameOrId)
        );
    }
}
