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

    @Column(name = "hex_id", unique = true, nullable = false)
    private String hexId;

    @Column(name = "short_hex_id", unique = true, nullable = false)
    private String shortHexId;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String layer;

    @Column(name = "star_count")
    private int starCount;

    @Column(name = "pull_count")
    private long pullCount;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "date_registered", nullable = false)
    private LocalDateTime dateRegistered;
    private String logoUrl;


    public static DockerOfficeImage from(DockerImageJson image, String tag) {
        String hexId = IdGenerator.generateHexFullId();
        
        return DockerOfficeImage.builder()
                .hexId(hexId)
                .shortHexId(IdGenerator.getShortId(hexId))
                .tag(tag)
                .name(image.getName())
                .description(image.getDescription())
                .layer(image.getName() + ":" + tag)
                .starCount(image.getStarCount())
                .pullCount(image.getPullCount())
                .lastUpdated(LocalDate.parse(image.getLastUpdated(), DATE_FORMATTER).atStartOfDay())
                .dateRegistered(
                    LocalDate.parse(image.getDateRegistered(), DATE_FORMATTER).atStartOfDay())
                .logoUrl(image.getLogoUrl())
                .build();
    }
}
