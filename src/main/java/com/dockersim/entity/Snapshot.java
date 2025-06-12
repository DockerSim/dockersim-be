package com.dockersim.entity;

import com.dockersim.entity.enums.SnapshotStateType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "snapshots")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Snapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String simulationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SnapshotStateType stateType = SnapshotStateType.MANUAL;

    @Column(columnDefinition = "JSON")
    private String containerState; // 컨테이너 상태 JSON

    @Column(columnDefinition = "JSON")
    private String imageState; // 이미지 상태 JSON

    @Column(columnDefinition = "JSON")
    private String networkState; // 네트워크 상태 JSON

    @Column(columnDefinition = "JSON")
    private String volumeState; // 볼륨 상태 JSON

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ContainerSimulation> containers = new ArrayList<>();

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImageSimulation> images = new ArrayList<>();

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NetworkSimulation> networks = new ArrayList<>();

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VolumeSimulation> volumes = new ArrayList<>();
}