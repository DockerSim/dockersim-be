package com.dockersim.service.container;

import org.springframework.stereotype.Component;

import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerContainerErrorCode;
import com.dockersim.repository.DockerContainerRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DockerContainerFinderImpl implements DockerContainerFinder {
	private final DockerContainerRepository repo;

	@Override
	public DockerContainer findByNameOrHexId(Simulation simulation, String containerNameOrHexId) {
		return repo.findByNameOrHexId(simulation, containerNameOrHexId).orElseThrow(
			() -> new BusinessException(DockerContainerErrorCode.NOT_FOUND_CONTAINER, containerNameOrHexId)
		);
	}
}
