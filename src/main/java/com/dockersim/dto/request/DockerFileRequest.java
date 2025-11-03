package com.dockersim.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DockerFileRequest {

    private String name;
    private String path;
    private String content;
}
