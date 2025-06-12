package com.dockersim.repository;

import com.dockersim.entity.NetworkSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NetworkSimulationRepository extends JpaRepository<NetworkSimulation, Long> {

    List<NetworkSimulation> findBySimulationId(String simulationId);

    Optional<NetworkSimulation> findBySimulationIdAndName(String simulationId, String name);

    @Query("SELECT n FROM NetworkSimulation n WHERE n.simulationId = :simulationId AND n.driver = :driver")
    List<NetworkSimulation> findBySimulationIdAndDriver(@Param("simulationId") String simulationId,
            @Param("driver") String driver);

    boolean existsBySimulationIdAndName(String simulationId, String name);

    long countBySimulationId(String simulationId);

    @Query("SELECT n FROM NetworkSimulation n WHERE n.simulationId = :simulationId AND " +
            "n.attachedContainers LIKE %:containerId%")
    List<NetworkSimulation> findNetworksAttachedToContainer(@Param("simulationId") String simulationId,
            @Param("containerId") String containerId);
}