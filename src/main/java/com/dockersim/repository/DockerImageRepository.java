package com.dockersim.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;

public interface DockerImageRepository extends JpaRepository<DockerImage, Long> {

	Optional<DockerImage> findBySimulationAndNamespaceAndNameAndTagAndLocation(
		Simulation simulation,
		String namespace,
		String name,
		String tag,
		ImageLocation location
	);

	List<DockerImage> findBySimulationAndNamespaceAndNameAndLocation(
		Simulation simulation,
		String namespace,
		String name,
		ImageLocation location
	);

	// -----------------------------------------------------------------

	Optional<DockerImage> findBySimulationAndHexIdStartsWithAndLocation(Simulation simulation, String hexId,
		ImageLocation location);

	@Query("SELECT d FROM DockerImage d "
		+ "WHERE d.simulation = :simulation "
		+ "AND d.location = 'LOCAL' "
		+ "AND d.name <> '<none>' "
		+ "AND d.tag <> '<none>'")
	List<DockerImage> findNotDanglingImageBySimulation(@Param("simulation") Simulation simulation);

	@Query("SELECT d FROM DockerImage d "
		+ "WHERE d.simulation = :simulation "
		+ "AND d.location = 'LOCAL'")
	List<DockerImage> findBySimulation(@Param("simulation") Simulation simulation);

	@Query("SELECT d FROM DockerImage d "
		+ "WHERE d.simulation = :simulation "
		+ "AND d.name = '<none>' "
		+ "AND d.tag = '<none>' "
		+ "AND d.location = 'LOCAL'")
	List<DockerImage> findDanglingImageBySimulationInLocal(@Param("simulation") Simulation simulation);

	@Query("SELECT d FROM DockerImage d "
		+ "WHERE d.simulation = :simulation "
		+ "AND d.containers IS EMPTY")
	List<DockerImage> findUnreferencedImageBySimulationInLocal(@Param("simulation") Simulation simulation);

	@Query("SELECT d FROM DockerImage  d "
		+ "WHERE d.simulation = :simulation "
		+ "AND d.namespace = :namespace "
		+ "AND d.name = :name "
		+ "AND d.location = 'HUB'")
	List<DockerImage> findAllBySimulationAndNamespaceAndNameInHub(@Param("simulation") Simulation simulation,
		@Param("namespace") String namespace, @Param("name") String name);

	@Query("SELECT d FROM DockerImage  d "
		+ "WHERE d.simulation = :simulation "
		+ "AND d.namespace = :namespace "
		+ "AND d.name = :name "
		+ "AND d.tag = :tag "
		+ "AND d.location = 'HUB'")
	Optional<DockerImage> findBySimulationAndNamespaceAndNameAndTagInHub(@Param("simulation") Simulation simulation,
		@Param("namespace") String namespace, @Param("name") String name, @Param("tag") String tag);

	// ---------------------------------------------------------------------------------------------------------

	Optional<DockerImage> findByNameAndNamespaceAndTagAndLocationAndSimulation(String name,
		String namespace, String tag, ImageLocation location, Simulation simulation);

	boolean existsByNameAndNamespaceAndTagAndLocationAndSimulation(String name, String namespace,
		String tag, ImageLocation location, Simulation simulation);

	List<DockerImage> findAllBySimulationAndLocation(Simulation simulation, ImageLocation location);

	/*
	image push -a
	 */
	@Query("SELECT d FROM DockerImage  d "
		+ "WHERE d.simulation = :simulation "
		+ "AND d.namespace = :namespace "
		+ "AND d.name = :name "
		+ "AND d.location = 'LOCAL'")
	List<DockerImage> findAllBySimulationAndNamespaceAndName(
		@Param("simulation") Simulation simulation,
		@Param("namespace") String namespace,
		@Param("name") String name
	);
}
