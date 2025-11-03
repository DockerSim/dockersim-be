package com.dockersim.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dockersim.domain.DockerVolume;
import com.dockersim.domain.Simulation;

public interface DockerVolumeRepository extends JpaRepository<DockerVolume, Long> {

	/*
	target
		- volume create
	 */
	boolean existsBySimulationAndName(Simulation simulation, String name);

	/*
	target
		- volume inspect
		- volume rm
	 */
	Optional<DockerVolume> findBySimulationAndName(Simulation simulation, String name);

	/*
	target
		- volume ls
	 */
	List<DockerVolume> findBySimulation(Simulation simulation);

	/*
	target
		- volume prune
		- volume prune --all
	 */
	@Query("SELECT v FROM DockerVolume v " +
		"WHERE v.simulation = :simulation " +
		"AND v.anonymous = :anonymous " +
		"AND v.containerVolumes IS EMPTY"
	)
	List<DockerVolume> findUnusedVolumes(
		@Param("simulation") Simulation simulation,
		@Param("anonymous") boolean anonymous
	);
}
