package com.dockersim.repository;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DockerContainerRepository extends JpaRepository<DockerContainer, Long> {

    @Query("""
            SELECT c FROM DockerContainer c
            WHERE c.simulation = :simulation
                AND (c.shortHexId = :nameOrHexId OR c.name = :nameOrHexId)
            """)
    Optional<DockerContainer> findByNameOrHexId(
            @Param("simulation") Simulation simulation,
            @Param("nameOrHexId") String nameOrHexId
    );

    @Query("""
            SELECT c
            FROM DockerContainer c
            WHERE c.simulation = :simulation
                AND (c.shortHexId = :nameOrHexId OR c.name = :nameOrHexId)
                AND c.status = :status
            """)
    Optional<DockerContainer> findBySimulationAndIdentifierAndStatus(
            @Param("simulation") Simulation simulation,
            @Param("nameOrHexId") String nameOrHexId,
            @Param("status") ContainerStatus status
    );

    List<DockerContainer> findAllBySimulation(Simulation simulation);

    List<DockerContainer> findAllBySimulationAndStatus(Simulation simulation, ContainerStatus status);
}
