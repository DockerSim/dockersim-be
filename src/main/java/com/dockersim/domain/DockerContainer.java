package com.dockersim.domain;

import com.dockersim.common.IdGenerator;
import com.dockersim.dto.request.CreateContainerRequest;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @Column(name = "simulation_id_str", nullable = false)
    private String simulationId;


    @Column(name = "image_id", nullable = false)
    private String baseImageId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "container_id", nullable = false)
    private String containerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContainerStatus status;

    private String hostPort;
    private String containerPort;

    @Column(columnDefinition = "TEXT")
    private String environment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "stopped_at")
    private LocalDateTime stoppedAt;


    public static DockerContainer from(CreateContainerRequest request) {
        return DockerContainer.builder()
            .containerId(IdGenerator.generateFullId())
            .baseImageId(request.getBaseImageId())
            .name(request.getName())
            .status(request.getStatus())
            .hostPort(request.getHostPort())
            .containerPort(request.getContainerPort())
            .environment(request.getEnvironment())
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
}
