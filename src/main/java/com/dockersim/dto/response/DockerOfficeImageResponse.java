package com.dockersim.dto.response;

import com.dockersim.domain.DockerOfficeImage;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerOfficeImageResponse {

    private String imageId;
    private String name;
    private String namespace;
    private String description;
    private int starCount;
    private long pullCount;
    private LocalDate lastUpdated;
    private LocalDate dateRegistered;
    private String logoUrl;
    private String tags;

    public static DockerOfficeImageResponse fromDockerOfficeImage(DockerOfficeImage image) {
        return DockerOfficeImageResponse.builder()
            .name(image.getName())
            .namespace(image.getNamespace())
            .description(image.getDescription())
            .starCount(image.getStarCount())
            .pullCount(image.getPullCount())
            .lastUpdated(image.getLastUpdated())
            .dateRegistered(image.getDateRegistered())
            .logoUrl(image.getLogoUrl())
            .tags(image.getTags())
            .build();
    }
}
