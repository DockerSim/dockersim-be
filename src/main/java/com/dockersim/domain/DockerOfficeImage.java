package com.dockersim.domain;

import com.dockersim.service.DockerImageJson;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerOfficeImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String namespace;
    private String description;
    private int starCount;
    private long pullCount;
    private LocalDate lastUpdated;
    private LocalDate dateRegistered;
    private String logoUrl;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DockerOfficeTag> tags = new ArrayList<>();

    public static DockerOfficeImage fromJson(DockerImageJson image) {
        DockerOfficeImage newImage = DockerOfficeImage.builder()
                .name(image.getName())
                .namespace(image.getNamespace())
                .description(image.getDescription())
                .starCount(image.getStarCount())
                .pullCount(image.getPullCount())
                .lastUpdated(LocalDate.parse(image.getLastUpdated()))
                .dateRegistered(LocalDate.parse(image.getDateRegistered()))
                .logoUrl(image.getLogoUrl())
                .build();

        image.getTags().forEach(tagStr -> {
            DockerOfficeTag tag = DockerOfficeTag.builder()
                    .tag(tagStr)
                    .image(newImage)
                    .build();
            newImage.getTags().add(tag);
        });

        return newImage;
    }
}
