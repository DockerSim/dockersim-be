package com.dockersim.dto.response;

import com.dockersim.domain.DockerOfficeImage;
import java.time.LocalDateTime;
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
    private LocalDateTime lastUpdated;
    private LocalDateTime dateRegistered;
    private String logoUrl;
    private String tag;

    public static DockerOfficeImageResponse from(DockerOfficeImage image) {
        return DockerOfficeImageResponse.builder()
            .name(image.getName())
            .namespace(image.getNamespace())
            .description(image.getDescription())
            .starCount(image.getStarCount())
            .pullCount(image.getPullCount())
            .lastUpdated(image.getLastUpdated())
            .dateRegistered(image.getDateRegistered())
            .logoUrl(image.getLogoUrl())
            .tag(image.getTag())
            .build();
    }
}
