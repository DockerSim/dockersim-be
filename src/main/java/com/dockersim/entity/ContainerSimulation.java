package com.dockersim.entity;

import com.dockersim.entity.enums.ContainerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "container_simulation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ContainerSimulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String simulationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imageName;

    private String imageTag = "latest";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContainerStatus status = ContainerStatus.CREATED;

    @Column(columnDefinition = "JSON")
    private String ports; // 포트 매핑 정보 JSON

    @Column(columnDefinition = "JSON")
    private String environment; // 환경변수 JSON

    @Column(columnDefinition = "JSON")
    private String volumes; // 볼륨 마운트 정보 JSON

    @Column(columnDefinition = "JSON")
    private String networks; // 네트워크 정보 JSON

    private String command;

    private String workingDir;

    private LocalDateTime startedAt;

    private LocalDateTime stoppedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id")
    private Snapshot snapshot;
}