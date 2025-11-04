package com.dockersim.domain;

import com.dockersim.common.IdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "docker_containers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false, updatable = false)
    private String hexId;

    @Column(unique = true, nullable = false, updatable = false)
    private String shortHexId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContainerStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY) // OntToOne
    @JoinColumn(name = "image_id")
    private DockerImage baseImage;

    @OneToMany(mappedBy = "container")
    private Set<ContainerVolume> containerVolumes = new HashSet<>();

    @OneToMany(mappedBy = "container")
    private Set<ContainerNetwork> containerNetworks = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime stoppedAt;

    /*
    target:
    - create
     */
    public static DockerContainer from(String name, DockerImage baseImage, Simulation simulation) {
        String hexId = IdGenerator.generateHexFullId();
        String shortHexId = IdGenerator.getShortId(hexId);

        return DockerContainer.builder()
                .hexId(hexId)
                .shortHexId(shortHexId)
                .name(name != null ? name : shortHexId)
                .status(ContainerStatus.EXITED)
                .baseImage(baseImage)
                .simulation(simulation)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public boolean start() {
        if (this.status == ContainerStatus.CREATED ||
                this.status == ContainerStatus.EXITED) {
            this.status = ContainerStatus.RUNNING;
            this.startedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public boolean kill() {
        if (this.status == ContainerStatus.RUNNING || this.status == ContainerStatus.PAUSED) {
            this.status = ContainerStatus.EXITED;
            this.stoppedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (this.status == ContainerStatus.RUNNING) {
            this.status = ContainerStatus.EXITED;
            this.stoppedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public boolean pause() {
        if (this.status == ContainerStatus.RUNNING) {
            this.status = ContainerStatus.PAUSED;
            return true;
        }
        return false;
    }

    public boolean unpause() {
        if (this.status == ContainerStatus.PAUSED) {
            this.status = ContainerStatus.RUNNING;
            return true;
        }
        return false;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
