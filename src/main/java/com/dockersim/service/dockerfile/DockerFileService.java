package com.dockersim.service.dockerfile;

import java.util.List;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.request.DockerFileRequest;
import com.dockersim.dto.response.DockerFileResponse;

public interface DockerFileService {

	DockerFileResponse createDockerFile(SimulationUserPrincipal principal,
		DockerFileRequest request);

	DockerFileResponse getDockerFileInfo(SimulationUserPrincipal principal, Long id);

	List<DockerFileResponse> getDockerFileSummary(SimulationUserPrincipal principal);

	DockerFileResponse updateDockerFile(SimulationUserPrincipal principal, Long id,
		DockerFileRequest request);

	void deleteDockerfile(SimulationUserPrincipal principal, Long id);
}
