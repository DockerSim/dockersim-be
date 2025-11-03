package com.dockersim.service.container;

import com.dockersim.domain.DockerContainer;
import com.dockersim.domain.Simulation;

public interface DockerContainerFinder {
	DockerContainer findByNameOrHexId(Simulation simulation, String networkNameOrHexId);
}
