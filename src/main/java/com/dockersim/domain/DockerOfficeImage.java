package com.dockersim.domain;

import com.dockersim.common.IdGenerator;
import com.dockersim.dto.DockerImageJson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docker_office_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerOfficeImage {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

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
    private LocalDateTime lastUpdated;

    @Column(name = "date_registered")
    private LocalDateTime dateRegistered;
    private String logoUrl;

    private String tag;

    public static DockerOfficeImage from(DockerImageJson image, String tag) {
        return DockerOfficeImage.builder()
            .name(image.getName())
            .imageId(IdGenerator.generateFullId())
            .namespace(image.getNamespace())
            .description(image.getDescription())
            .starCount(image.getStarCount())
            .pullCount(image.getPullCount())
            .lastUpdated(LocalDate.parse(image.getLastUpdated(), DATE_FORMATTER).atStartOfDay())
            .dateRegistered(
                LocalDate.parse(image.getDateRegistered(), DATE_FORMATTER).atStartOfDay())
            .logoUrl(image.getLogoUrl())
            .tag(tag)
            .build();
    }
}
