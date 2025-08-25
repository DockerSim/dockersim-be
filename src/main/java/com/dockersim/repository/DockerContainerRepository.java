package com.dockersim.repository;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerContainerRepository extends JpaRepository<DockerContainer, Long> {

    boolean existsByBaseImageIdAndStatus(String imageId, ContainerStatus status);

    Optional<DockerContainer> findByNameAndStatusAndSimulationId(String name,
        ContainerStatus status, String simulationId);

    Optional<DockerContainer> findByContainerIdAndStatusAndSimulationId(String containerId,
        ContainerStatus status, String simulationId);

    List<DockerContainer> findAllBySimulation(Simulation simulation);

    Optional<DockerContainer> findByNameAndSimulation(String name, Simulation simulation);

    Optional<DockerContainer> findByContainerIdAndSimulation(String containerId,
        Simulation simulation);
}
