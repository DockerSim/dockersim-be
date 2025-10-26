package com.dockersim.service.network;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dockersim.domain.ContainerNetwork;
import com.dockersim.domain.DockerNetwork;
import com.dockersim.domain.Simulation;

public interface DockerNetworkRepository extends JpaRepository<DockerNetwork, Long> {

	@Query("""
			SELECT d FROM DockerNetwork d
			WHERE d.simulation = :simulation
			  AND (
				   d.shortHexId = :nameOrHexId
				OR d.name       = :nameOrHexId
				OR d.hexId LIKE CONCAT(:nameOrHexId, '%')
			  )
		""")
	Optional<DockerNetwork> findByNameOrHexId(
		@Param("simulation") Simulation simulation,
		@Param("nameOrHexId") String nameOrHexId
	);

	List<DockerNetwork> findBySimulation(Simulation simulation);

	boolean existsBySimulationAndName(Simulation simulation, String name);

	@Query("""
			SELECT cn FROM ContainerNetwork cn
			JOIN FETCH cn.container c
			JOIN cn.network n
			WHERE n.id = :networkId AND n.simulation.id = :simulationId
		""")
	List<ContainerNetwork> findByNetworkIdWithContainerInSimulation(
		@Param("networkId") Long networkId,
		@Param("simulationId") Long simulationId
	);

	@Query("SELECT d FROM DockerNetwork d " +
		"WHERE d.simulation = :simulation " +
		"AND d.containerNetworks IS EMPTY")
	List<DockerNetwork> findUnusedNetworks(@Param("simulation") Simulation simulation);
}
