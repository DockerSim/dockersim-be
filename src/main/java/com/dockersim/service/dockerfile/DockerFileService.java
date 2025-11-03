package com.dockersim.service.dockerfile;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.request.DockerFileRequest;
import com.dockersim.dto.response.DockerFileResponse;
import java.util.List;

public interface DockerFileService {

    DockerFileResponse create(SimulationUserPrincipal principal,
        DockerFileRequest request);

    DockerFileResponse get(SimulationUserPrincipal principal, Long id);

    List<DockerFileResponse> getAll(SimulationUserPrincipal principal);

    DockerFileResponse update(SimulationUserPrincipal principal, Long id,
        DockerFileRequest request);

    void delete(SimulationUserPrincipal principal, Long id);
}
