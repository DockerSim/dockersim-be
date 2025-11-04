package com.dockersim.service.container;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;

import java.util.List;

public interface DockerContainerFinder {
    DockerContainer findByIdentifier(Simulation simulation, String nameOrHexId);

    DockerContainer findBySimulationAndIdentifierAndStatus(Simulation simulation, String nameOrHexId, ContainerStatus status);

    List<DockerContainer> findBySimulation(Simulation simulation, boolean all);
}
