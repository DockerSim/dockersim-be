package com.dockersim.entity;

import com.dockersim.entity.enums.ImageSource;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "image_simulation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ImageSimulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String simulationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tag;

    private String namespace; // Docker 이미지의 네임스페이스 (예: library, username)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageSource source = ImageSource.PULLED;

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