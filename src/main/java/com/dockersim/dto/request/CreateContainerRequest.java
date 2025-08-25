package com.dockersim.dto.request;


import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateContainerRequest {

    String baseImageId;
    String name;
    ContainerStatus status;
    String hostPort;
    String containerPort;
    String environment;

    public CreateContainerRequest(DockerImage baseImage, String name, ContainerStatus status,
        String hostPort, String containerPort, String environment) {
        this.baseImageId = baseImage.getImageId();
        this.name = name;
        this.status = status;
        this.hostPort = hostPort;
        this.containerPort = containerPort;
        this.environment = environment;
    }
}
