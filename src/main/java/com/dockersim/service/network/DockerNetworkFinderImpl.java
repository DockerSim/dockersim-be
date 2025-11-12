package com.dockersim.service.network;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dockersim.domain.ContainerNetwork;
import com.dockersim.domain.DockerNetwork;
import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerNetworkErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import Slf4j

@Slf4j // Add Slf4j annotation
@Component
@RequiredArgsConstructor
public class DockerNetworkFinderImpl implements DockerNetworkFinder {
	private final DockerNetworkRepository repo;

	@Override
	public DockerNetwork findByNameOrHexId(Simulation simulation, String nameOrHexId) {
		log.debug("findByNameOrHexId: Attempting to find network by nameOrHexId='{}' for simulation='{}'", nameOrHexId, simulation.getPublicId());
		return repo.findByNameOrHexId(simulation, nameOrHexId).orElseThrow(
			() -> {
				log.warn("findByNameOrHexId: Network not found: nameOrHexId='{}', simulation='{}'", nameOrHexId, simulation.getPublicId());
				return new BusinessException(DockerNetworkErrorCode.NOT_FOUND_NETWORK, nameOrHexId);
			}
		);
	}

	@Override
	public List<DockerNetwork> findAll(Simulation simulation) {
		return repo.findBySimulation(simulation);
	}

	@Override
	public boolean existsByName(Simulation simulation, String name) {
		return repo.existsBySimulationAndName(simulation, name);
	}

	@Override
	public List<ContainerNetwork> getDockerContainerSimpleInspects(
		Long networkId, Long simulationId
	) {
		return repo.findByNetworkIdWithContainerInSimulation(networkId, simulationId);
	}

	@Override
	public List<DockerNetwork> findUnusedNetworks(Simulation simulation) {
		return repo.findUnusedNetworks(simulation);
	}
}
