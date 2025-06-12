package com.dockersim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "registry_image_simulation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RegistryImageSimulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tag;

    private String digest;

    private Long size; // bytes

    private Long pullCount = 0L;

    private Long pushCount = 0L;

    @Column(columnDefinition = "TEXT")
    private String dockerfile;

    @Column(columnDefinition = "JSON")
    private String manifest; // 이미지 매니페스트 JSON

    @Column(columnDefinition = "JSON")
    private String config; // 이미지 설정 JSON

    private LocalDateTime lastPulledAt;

    private LocalDateTime lastPushedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registry_id", nullable = false)
    private RegistrySimulation registry;
}