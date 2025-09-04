package com.dockersim.dto.request;


import com.dockersim.domain.ContainerStatus;
import java.util.List;
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
    String ports;
    String bindVolumes;
    String envs;

    public CreateContainerRequest(String baseImage, String name, ContainerStatus status,
        List<String> port, List<String> volume, List<String> env) {
        this.baseImageId = baseImage;
        this.name = name;
        this.status = status;
        this.ports = String.join(",", port);
        this.bindVolumes = String.join(",", volume);
        this.envs = String.join(",", env);
    }
}
