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

    private Long id;
    private String hexId;
    private String shortHexId;
    private String namespace;
    private String name;
    private String tag;
    private String description;
    private String layer;
    private int starCount;
    private long pullCount;
    private LocalDateTime lastUpdated;
    private LocalDateTime dateRegistered;
    private String logoUrl;

    public static DockerOfficeImageResponse from(DockerOfficeImage image) {
        return DockerOfficeImageResponse.builder()
                .id(image.getId())
                .hexId(image.getHexId())
                .shortHexId(image.getShortHexId())
                .namespace("library")
                .name(image.getName())
                .tag(image.getTag())
                .description(image.getDescription())
                .layer(image.getLayer())
                .starCount(image.getStarCount())
                .pullCount(image.getPullCount())
                .lastUpdated(image.getLastUpdated())
                .dateRegistered(image.getDateRegistered())
                .logoUrl(image.getLogoUrl())
                .build();
    }
}
