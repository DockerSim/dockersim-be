package com.dockersim.service.network;

import java.util.List;

import com.dockersim.domain.ContainerNetwork;
import com.dockersim.domain.DockerNetwork;
import com.dockersim.domain.Simulation;

public interface DockerNetworkFinder {
	DockerNetwork findByNameOrHexId(Simulation simulation, String nameOrHexId);

	List<DockerNetwork> findAll(Simulation simulation);

	boolean existsByName(Simulation simulation, String name);

	List<ContainerNetwork> getDockerContainerSimpleInspects(Long networkId, Long simulationId);

	List<DockerNetwork> findUnusedNetworks(Simulation simulation);
}
