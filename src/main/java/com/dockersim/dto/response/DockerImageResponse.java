package com.dockersim.dto.response;

import com.dockersim.domain.DockerImage;
import java.time.LocalDate;
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
    private LocalDate created;
    private String logoUrl;
    private String tags;

    public static DockerImageResponse fromDockerImage(DockerImage image, List<String> console) {
        return DockerImageResponse.builder()
            .console(console)
            .imageId(image.getImageId())
            .name(image.getName())
            .namespace(image.getNamespace())
            .created(image.getCreated())
            .tags(image.getTags())
            .build();
    }
}
