package com.dockersim.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;

public interface DockerContainerRepository extends JpaRepository<DockerContainer, Long> {

	@Query("""
			SELECT c FROM DockerContainer c
			WHERE c.simulation = :simulation
			  AND (
				   c.shortHexId = :nameOrHexId
				OR c.name       = :nameOrHexId
				OR c.hexId LIKE CONCAT(:nameOrHexId, '%')
			  )
		""")
	Optional<DockerContainer> findByNameOrHexId(
		@Param("simulation") Simulation simulation,
		@Param("nameOrHexId") String nameOrHexId
	);

}
