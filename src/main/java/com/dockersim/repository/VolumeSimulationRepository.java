package com.dockersim.repository;

import com.dockersim.entity.VolumeSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolumeSimulationRepository extends JpaRepository<VolumeSimulation, Long> {

    List<VolumeSimulation> findBySimulationId(String simulationId);

    Optional<VolumeSimulation> findBySimulationIdAndName(String simulationId, String name);

    @Query("SELECT v FROM VolumeSimulation v WHERE v.simulationId = :simulationId AND v.driver = :driver")
    List<VolumeSimulation> findBySimulationIdAndDriver(@Param("simulationId") String simulationId,
            @Param("driver") String driver);

    boolean existsBySimulationIdAndName(String simulationId, String name);

    long countBySimulationId(String simulationId);

    @Query("SELECT v FROM VolumeSimulation v WHERE v.simulationId = :simulationId AND " +
            "v.attachedContainers LIKE %:containerId%")
    List<VolumeSimulation> findVolumesAttachedToContainer(@Param("simulationId") String simulationId,
            @Param("containerId") String containerId);
}