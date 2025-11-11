package com.dockersim.domain;

import com.dockersim.common.IdGenerator;
import com.dockersim.dto.response.DockerOfficeImageResponse; // Import DockerOfficeImageResponse
import com.dockersim.dto.util.ImageMeta;
import com.dockersim.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "hex_id", nullable = false, updatable = false)
    private String hexId;

    @Column(name = "short_hex_id", nullable = false, updatable = false)
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

    @Convert(converter = StringListConverter.class)
    private List<String> layers;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;

    @OneToMany(mappedBy = "baseImage")
    private List<DockerContainer> containers = new ArrayList<>();

    /*
    target:
        image build
     */
    public static DockerImage from(Simulation simulation, DockerFile dockerFile,
                                   ImageMeta meta) {

        String hexId = IdGenerator.generateHexFullId();
        String shortHexId = IdGenerator.getShortId(hexId);
        String name = meta.getName().isEmpty() ? shortHexId : meta.getName();
        List<String> layers = new ArrayList<>(List.of(dockerFile.getContent().split("\n")));
        layers.add("0B");

        return DockerImage.builder()
                .hexId(hexId)
                .shortHexId(shortHexId)
                .namespace(meta.getNamespace())
                .name(name)
                .tag(meta.getTag())
                .location(ImageLocation.LOCAL)
                .layers(layers)
                .createdAt(LocalDateTime.now())
                .simulation(simulation)
                .build();
    }

    /*
    target:
        image push
     */
    public static DockerImage from(DockerImage oldImage, ImageLocation location) {
        return oldImage.toBuilder()
                .location(location)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /*
    target:
    - office image load
     */
    public static DockerImage from(DockerOfficeImage officeImage, ImageLocation location) {
        return DockerImage.builder()
                .hexId(officeImage.getHexId())
                .shortHexId(officeImage.getShortHexId())
                .namespace("library")
                .name(officeImage.getName())
                .tag(officeImage.getTag())
                .location(location)
                .layers(List.of(officeImage.getName()))
                .createdAt(officeImage.getLastUpdated())
                .build();
    }

    // New from method for DockerOfficeImageResponse
    public static DockerImage from(DockerOfficeImageResponse officeImageResponse, Simulation simulation, ImageLocation location) {
        return DockerImage.builder()
                .hexId(officeImageResponse.getHexId())
                .shortHexId(officeImageResponse.getShortHexId())
                .namespace("library") // Assuming official images are always in 'library' namespace
                .name(officeImageResponse.getName())
                .tag(officeImageResponse.getTag())
                .location(location)
                .layers(List.of(officeImageResponse.getName())) // Placeholder for layers
                .createdAt(officeImageResponse.getLastUpdated())
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

    public void addSimulation(Simulation newSimulation) {
        if (this.simulation != null) {
            this.simulation.getDockerImages().remove(this);
        }
        this.simulation = newSimulation;
        this.simulation.getDockerImages().add(this);
    }

    public void removeSimulation() {
        if (this.simulation != null) {
            this.simulation.getDockerImages().remove(this);
        }
    }
}
