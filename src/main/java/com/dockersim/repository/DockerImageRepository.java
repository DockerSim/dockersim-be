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

	Optional<DockerImage> findBySimulationAndNamespaceAndNameAndTagAndLocation(Simulation simulation, String namespace,
		String name,
		String tag,
		ImageLocation location);

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

	// ------------

	Optional<DockerImage> findByNameAndTagAndSimulation(String name, String tag,
		Simulation simulation);

	Optional<DockerImage> findByImageIdAndSimulation(String imageId, Simulation simulation);

	List<DockerImage> findAllByImageIdStartingWithAndSimulation(String shortImageId,
		Simulation simulation);

	List<DockerImage> findAllByNameAndSimulation(String name, Simulation simulation);

	// --- New methods for location-based search ---

	Optional<DockerImage> findByNameAndTagAndLocationAndSimulation(String name, String tag,
		ImageLocation location, Simulation simulation);

	Optional<DockerImage> findByNameAndNamespaceAndTagAndLocationAndSimulation(String name,
		String namespace, String tag, ImageLocation location, Simulation simulation);

	boolean existsByNameAndTagAndLocationAndSimulation(String name, String tag,
		ImageLocation location, Simulation simulation);

	boolean existsByNameAndNamespaceAndTagAndLocationAndSimulation(String name, String namespace,
		String tag, ImageLocation location, Simulation simulation);

	List<DockerImage> findAllBySimulationAndLocation(Simulation simulation, ImageLocation location);

}
