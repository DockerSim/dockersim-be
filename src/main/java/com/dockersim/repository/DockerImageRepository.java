package com.dockersim.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;

public interface DockerImageRepository extends JpaRepository<DockerImage, Long> {

	Optional<DockerImage> findByNamespaceAndNameAndTagAndLocation(String namespace, String name,
		String tag,
		ImageLocation location);

	Optional<DockerImage> findByHexIdStartsWithAndLocation(String hexId, ImageLocation location);

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
