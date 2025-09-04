package com.dockersim.domain;

import com.dockersim.common.IdGenerator;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
@Builder(toBuilder = true)
public class DockerImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hex_id", nullable = false, unique = true, updatable = false)
    private String hexId;

    @Column(name = "short_hex_id", nullable = false, unique = true, updatable = false)
    private String shortHexId;

    @Column(nullable = false)
    private String namespace;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageLocation location;

    private String layer;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @OneToMany(mappedBy = "baseImage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DockerContainer> containers = new ArrayList<>();

    public static DockerImage from(DockerOfficeImage officeImage, Simulation simulation) {
        return DockerImage.builder()
            .hexId(officeImage.getHexId())
            .hexId(officeImage.getShortHexId())
            .namespace(officeImage.getNamespace())
            .name(officeImage.getName())
            .tag(officeImage.getTag())
            .location(ImageLocation.LOCAL)
            .layer(null)
            .createdAt(officeImage.getLastUpdated())
            .simulation(simulation)
            .build();
    }

    public static DockerImage from(DockerImage pullImage, Simulation simulation,
        ImageLocation location) {
        return pullImage.toBuilder()
            .simulation(simulation)
            .location(location)
            .build();
    }

    public static DockerImage from(Simulation simulation, DockerFile dockerFile,
        Map<String, String> imageNameMap) {
        String hexId = IdGenerator.generateHexFullId();
        return DockerImage.builder()
            .hexId(hexId)
            .shortHexId(IdGenerator.getShortId(hexId))
            .namespace(imageNameMap.get("namespace"))
            .name(imageNameMap.get("repository"))
            .tag(imageNameMap.get("tag"))
            .location(ImageLocation.LOCAL)
            .layer(dockerFile.getContent())
            .createdAt(LocalDateTime.now())
            .simulation(simulation)
            .build();
    }

    public String getFullNameWithTag() {
        if ("<none>".equals(name)) {
            return "<none>:<none>";
        }
        return namespace + "/" + name + ":" + tag;
    }

    public void convertToDangling() {
        this.name = "<none>";
        this.tag = "<none>";
    }
}
