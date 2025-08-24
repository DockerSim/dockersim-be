package com.dockersim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "docker_images")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_id", nullable = false, unique = true)
    private String imageId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String namespace;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    public static DockerImage from(DockerOfficeImage officeImage, Simulation simulation) {
        return DockerImage.builder()
            .imageId(officeImage.getImageId())
            .name(officeImage.getName())
            .namespace(officeImage.getNamespace())
            .createdAt(officeImage.getLastUpdated())
            .tag(officeImage.getTag())
            .location(ImageLocation.LOCAL)
            .simulation(simulation)
            .build();
    }

    public static DockerImage from(DockerImage image, Simulation simulation,
        ImageLocation location) {
        return DockerImage.builder()
            .imageId(image.getImageId())
            .name(image.getName())
            .namespace(image.getNamespace())
            .createdAt(image.getCreatedAt())
            .tag(image.getTag())
            .location(location)
            .simulation(simulation)
            .build();
    }

    public String getFullName() {
        return namespace + "/" + name;
    }

    public String getFullNameWithTag() {
        if ("<none>".equals(name)) {
            return "<none>:<none>";
        }
        return getFullName() + ":" + tag;
    }

    public void convertToDangling() {
        this.name = "<none>";
        this.tag = "<none>";
    }
}
