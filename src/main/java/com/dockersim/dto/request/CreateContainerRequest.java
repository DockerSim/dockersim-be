package com.dockersim.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateContainerRequest {

    String baseImageId;
    String name;
    String ports;
    String bindVolumes;
    String envs;

    public CreateContainerRequest(String baseImage, String name, List<String> port, List<String> volume, List<String> env) {
        this.baseImageId = baseImage;
        this.name = name;
        this.ports = String.join(",", port);
        this.bindVolumes = String.join(",", volume);
        this.envs = String.join(",", env);
    }
}
