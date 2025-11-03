package com.dockersim.dto.response;

import com.dockersim.domain.ContainerStatus;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerContainerResponse {

    private List<String> console;

    private String name;
    private String containerId;
    private String baseImageId;
    private ContainerStatus status;
    private String port;
    private String volume;
    private String env;
    private List<String> layer;
}
