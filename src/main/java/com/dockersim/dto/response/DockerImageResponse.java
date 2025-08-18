package com.dockersim.dto.response;

import com.dockersim.domain.DockerImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerImageResponse {

    private List<String> console;
    private boolean success;

    private String imageId;
    private String name;
    private String namespace;
    private String description;
    private int starCount;
    private long pullCount;
    private LocalDateTime createdAt;
    private String logoUrl;
    private String tag;

    public static DockerImageResponse from(DockerImage image, List<String> console) {
        return DockerImageResponse.builder()
            .console(console)
            .imageId(image.getImageId())
            .name(image.getName())
            .namespace(image.getNamespace())
            .createdAt(image.getCreatedAt())
            .tag(image.getTag())
            .build();
    }
}