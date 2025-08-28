package com.dockersim.dto.response;

import com.dockersim.domain.DockerFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DockerFileResponse {
    private Long id;
    private String name;
    private String path;
    private String content;

    public static DockerFileResponse from(DockerFile dockerFile) {
        return DockerFileResponse.builder()
            .id(dockerFile.getId())
            .name(dockerFile.getName())
            .path(dockerFile.getPath())
            .content(dockerFile.getContent())
            .build();
    }
}
