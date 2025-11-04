package com.dockersim.dto.response;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import lombok.Builder;

@Builder
public class ContainerInspectData {
    public String id;
    public String createdAt;
    public String startedAt;
    public String stoppedAt;
    public ContainerStatus state;
    public String baseImage;

    public static ContainerInspectData from(DockerContainer container) {
        return ContainerInspectData.builder()
                .id(container.getHexId())
                .createdAt(container.getCreatedAt().toString())
                .startedAt(container.getStartedAt() != null ?
                        container.getStartedAt().toString() : "")
                .stoppedAt(container.getStoppedAt() != null ?
                        container.getStoppedAt().toString() : "")
                .state(container.getStatus())
                .baseImage(container.getBaseImage().getHexId())
                .build();
    }
}
