// 이 클래스는 Docker 컨테이너 정보를 저장하는 JPA 엔티티입니다.
// 주요 필드:
// - id : 엔티티 ID
// - containerId : Docker 컨테이너 ID (12자리 해시)
// - name : 컨테이너 이름
// - imageName : 사용된 이미지 이름
// - status : 컨테이너 상태 (RUNNING, STOPPED, CREATED 등)
// - ports : 포트 매핑 정보
// - environment : 환경 변수 목록

package com.dockersim.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "containers")
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "container_id", unique = true, nullable = false)
    private String containerId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContainerStatus status;

    @ElementCollection
    @CollectionTable(name = "container_ports", joinColumns = @JoinColumn(name = "container_id"))
    @Column(name = "port_mapping")
    private List<String> ports = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "container_environment", joinColumns = @JoinColumn(name = "container_id"))
    @Column(name = "env_variable")
    private List<String> environment = new ArrayList<>();

    @Column(name = "command")
    private String command;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "stopped_at")
    private LocalDateTime stoppedAt;

    @Column(name = "detached")
    private boolean detached;

    @Column(name = "auto_remove")
    private boolean autoRemove;

    // 기본 생성자
    protected Container() {
    }

    // 생성자
    public Container(String containerId, String name, String imageName, ContainerStatus status) {
        this.containerId = containerId;
        this.name = name;
        this.imageName = imageName;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getter/Setter 메서드들
    public Long getId() {
        return id;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public ContainerStatus getStatus() {
        return status;
    }

    public void setStatus(ContainerStatus status) {
        this.status = status;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    public List<String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(List<String> environment) {
        this.environment = environment;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(LocalDateTime stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    public boolean isDetached() {
        return detached;
    }

    public void setDetached(boolean detached) {
        this.detached = detached;
    }

    public boolean isAutoRemove() {
        return autoRemove;
    }

    public void setAutoRemove(boolean autoRemove) {
        this.autoRemove = autoRemove;
    }

    // 비즈니스 메서드들
    public void start() {
        this.status = ContainerStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void stop() {
        this.status = ContainerStatus.STOPPED;
        this.stoppedAt = LocalDateTime.now();
    }
}