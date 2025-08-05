package com.dockersim.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 컨테이너 엔티티
 */
@Entity
@Table(name = "simulation_containers") // 테이블명은 그대로 유지
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Container extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Docker 컨테이너 ID (랜덤 생성)
     */
    @Column(name = "container_id", nullable = false, unique = true)
    private String containerId;

    /**
     * 컨테이너 이름
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 기반 이미지
     */
    @Column(name = "image", nullable = false)
    private String image;

    /**
     * 컨테이너 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContainerStatus status;

    /**
     * 소속 시뮬레이션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;

    /**
     * 포트 매핑 정보 (JSON 형태로 저장)
     */
    @ElementCollection
    @CollectionTable(name = "container_port_mappings", joinColumns = @JoinColumn(name = "container_id"))
    private List<String> portMappings = new ArrayList<>();

    /**
     * 볼륨 마운트 정보
     */
    @ElementCollection
    @CollectionTable(name = "container_volume_mounts", joinColumns = @JoinColumn(name = "container_id"))
    private List<String> volumeMounts = new ArrayList<>();

    /**
     * 환경 변수
     */
    @ElementCollection
    @CollectionTable(name = "container_env_vars", joinColumns = @JoinColumn(name = "container_id"))
    private List<String> environmentVariables = new ArrayList<>();

    /**
     * 네트워크 정보
     */
    @Column(name = "network")
    private String network = "bridge"; // 기본 네트워크

    /**
     * 컨테이너 생성 시간
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Builder
    public Container(String containerId, String name, String image,
            ContainerStatus status, Simulation simulation) {
        this.containerId = containerId;
        this.name = name;
        this.image = image;
        this.status = status;
        this.simulation = simulation;
        this.startedAt = LocalDateTime.now();
    }

    // === 비즈니스 메서드 ===

    /**
     * 컨테이너 시작
     */
    public void start() {
        if (this.status == ContainerStatus.CREATED || this.status == ContainerStatus.EXITED) {
            this.status = ContainerStatus.RUNNING;
            this.startedAt = LocalDateTime.now();
        }
    }

    /**
     * 컨테이너 중지
     */
    public void stop() {
        if (this.status == ContainerStatus.RUNNING) {
            this.status = ContainerStatus.EXITED;
        }
    }

    /**
     * 컨테이너 재시작
     */
    public void restart() {
        this.status = ContainerStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * 포트 매핑 추가
     */
    public void addPortMapping(String portMapping) {
        this.portMappings.add(portMapping);
    }

    /**
     * 볼륨 마운트 추가
     */
    public void addVolumeMount(String volumeMount) {
        this.volumeMounts.add(volumeMount);
    }

    /**
     * 환경 변수 추가
     */
    public void addEnvironmentVariable(String envVar) {
        this.environmentVariables.add(envVar);
    }

    /**
     * 네트워크 설정
     */
    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * 실행 중인지 확인
     */
    public boolean isRunning() {
        return this.status == ContainerStatus.RUNNING;
    }

    /**
     * 중지된 상태인지 확인
     */
    public boolean isStopped() {
        return this.status == ContainerStatus.EXITED;
    }

    /**
     * 컨테이너 ID의 짧은 형태 반환 (Docker CLI와 유사)
     */
    public String getShortContainerId() {
        return containerId.length() > 12 ? containerId.substring(0, 12) : containerId;
    }
}