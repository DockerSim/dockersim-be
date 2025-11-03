package com.dockersim.service.network;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dockersim.domain.ContainerNetwork;
import com.dockersim.domain.DockerNetwork;
import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerNetworkErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DockerNetworkFinderImpl implements DockerNetworkFinder {
	private final DockerNetworkRepository repo;

	@Override
	public DockerNetwork findByNameOrHexId(Simulation simulation, String nameOrHexId) {
		return repo.findByNameOrHexId(simulation, nameOrHexId).orElseThrow(
			() -> new BusinessException(DockerNetworkErrorCode.NOT_FOUND_NETWORK, nameOrHexId)
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
