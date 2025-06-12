package com.dockersim.entity;

import com.dockersim.entity.enums.MountType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "volume_simulation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VolumeSimulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String simulationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String driver = "local";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MountType mountType = MountType.VOLUME;

    private String source;

    private String destination;

    @Column(columnDefinition = "JSON")
    private String options; // 볼륨 옵션 JSON

    @Column(columnDefinition = "JSON")
    private String attachedContainers; // 마운트된 컨테이너들 JSON

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