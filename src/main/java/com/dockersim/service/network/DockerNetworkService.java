package com.dockersim.service.network;

import java.util.List;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.DockerNetworkResponse;

public interface DockerNetworkService {

	DockerNetworkResponse connect(
		SimulationUserPrincipal principal,
		String networkNameOrHexId, String containerNameOrHexId
	);

	DockerNetworkResponse create(SimulationUserPrincipal principal,
		String networkName
	);

	DockerNetworkResponse disconnect(
		SimulationUserPrincipal principal,
		String networkNameOrHexId, String containerNameOrHexId, boolean force
	);

	List<String> inspect(SimulationUserPrincipal principal,
		String networkNameOrHexId
	);

	List<String> ls(SimulationUserPrincipal principal,
		boolean quiet
	);

	List<DockerNetworkResponse> prune(SimulationUserPrincipal principal);

	DockerNetworkResponse rm(SimulationUserPrincipal principal,
		String networkNameOrHexId
	);
}
