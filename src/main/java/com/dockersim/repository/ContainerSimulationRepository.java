package com.dockersim.repository;

import com.dockersim.entity.ContainerSimulation;
import com.dockersim.entity.enums.ContainerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContainerSimulationRepository extends JpaRepository<ContainerSimulation, Long> {

    List<ContainerSimulation> findBySimulationId(String simulationId);

    List<ContainerSimulation> findBySimulationIdAndStatus(String simulationId, ContainerStatus status);

    Optional<ContainerSimulation> findBySimulationIdAndName(String simulationId, String name);

    @Query("SELECT c FROM ContainerSimulation c WHERE c.simulationId = :simulationId AND c.status IN :statuses")
    List<ContainerSimulation> findBySimulationIdAndStatusIn(@Param("simulationId") String simulationId,
            @Param("statuses") List<ContainerStatus> statuses);

    @Query("SELECT c FROM ContainerSimulation c WHERE c.simulationId = :simulationId AND c.imageName = :imageName")
    List<ContainerSimulation> findBySimulationIdAndImageName(@Param("simulationId") String simulationId,
            @Param("imageName") String imageName);

    boolean existsBySimulationIdAndName(String simulationId, String name);

    long countBySimulationIdAndStatus(String simulationId, ContainerStatus status);
}