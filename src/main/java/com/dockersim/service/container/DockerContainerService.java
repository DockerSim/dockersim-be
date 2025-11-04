package com.dockersim.service.container;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.DockerContainerResponse;

import java.util.List;

public interface DockerContainerService {

    DockerContainerResponse create(SimulationUserPrincipal principal,
                                   String imageNameOrHexId, String name);

    List<String> inspect(SimulationUserPrincipal principal,
                         String containerNameOrHexId);

    List<String> ps(SimulationUserPrincipal principal,
                    boolean all, boolean quiet);

    DockerContainerResponse pause(SimulationUserPrincipal principal, String containerNameOrHexId);

    List<DockerContainerResponse> restart(SimulationUserPrincipal principal,
                                          String containerNameOrHexId);

    DockerContainerResponse rm(SimulationUserPrincipal principal,
                               String containerNameOrHexId);

    DockerContainerResponse start(SimulationUserPrincipal principal,
                                  String containerNameOrHexId);

    DockerContainerResponse stop(SimulationUserPrincipal principal,
                                 String containerNameOrHexId);

    DockerContainerResponse unpause(SimulationUserPrincipal principal,
                                    String containerNameOrHexId);

}