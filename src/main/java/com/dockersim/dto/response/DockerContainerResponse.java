package com.dockersim.dto.response;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerContainerResponse {

    private List<String> console;

    private Long id;
    private String hexId;
    private String shortHexId;
    private String name;
    private String baseImageName;
    private ContainerStatus status;
    private List<String> layer;

    public static DockerContainerResponse from(List<String> console, DockerContainer container) {
        return DockerContainerResponse.builder()
                .console(console)
                .id(container.getId())
                .hexId(container.getHexId())
                .shortHexId(container.getShortHexId())
                .name(container.getName())
                .baseImageName(container.getBaseImage().getName())
                .layer(container.getBaseImage().getLayers())
                .status(container.getStatus())
                .build();
    }
}
