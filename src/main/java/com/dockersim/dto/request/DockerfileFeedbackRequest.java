package com.dockersim.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DockerfileFeedbackRequest {

    @NotBlank(message = "Dockerfile 내용이 비어있습니다.")
    private String dockerfileContent;
}
