package com.dockersim.domain;

import com.dockersim.common.IdGenerator;
import com.dockersim.service.DockerImageJson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerOfficeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_id")
    private String imageId;

    private String name;
    private String namespace;
    private String description;

    @Column(name = "star_count")
    private int starCount;

    @Column(name = "pull_count")
    private long pullCount;

    @Column(name = "last_updated")
    private LocalDate lastUpdated;

    @Column(name = "date_registered")
    private LocalDate dateRegistered;
    private String logoUrl;
    private String tags;

    public static DockerOfficeImage fromJson(DockerImageJson image) {
        return DockerOfficeImage.builder()
            .name(image.getName())
            .imageId(IdGenerator.generateFullId())
            .namespace(image.getNamespace())
            .description(image.getDescription())
            .starCount(image.getStarCount())
            .pullCount(image.getPullCount())
            .lastUpdated(LocalDate.parse(image.getLastUpdated()))
            .dateRegistered(LocalDate.parse(image.getDateRegistered()))
            .logoUrl(image.getLogoUrl())
//            .tags(List.of(image.getTags()).toString())
            .build();
    }
}
