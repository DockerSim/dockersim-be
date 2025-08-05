package com.dockersim.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 이미지 엔티티
 */
@Entity
@Table(name = "simulation_images") // 테이블명은 그대로 유지
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Docker 이미지 ID (랜덤 생성)
     */
    @Column(name = "image_id", nullable = false, unique = true)
    private String imageId;

    /**
     * 이미지 저장소명
     */
    @Column(name = "repository", nullable = false)
    private String repository;

    /**
     * 이미지 태그
     */
    @Column(name = "tag", nullable = false)
    private String tag;

    /**
     * 소속 시뮬레이션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;

    /**
     * 이미지 생성 시간
     */
    @Column(name = "image_created_at")
    private LocalDateTime imageCreatedAt;

    @Builder
    public Image(String imageId, String repository, String tag, Simulation simulation) {
        this.imageId = imageId;
        this.repository = repository;
        this.tag = tag;
        this.simulation = simulation;
        this.imageCreatedAt = LocalDateTime.now();
    }

    // === 비즈니스 메서드 ===

    /**
     * 전체 이미지명 반환 (repository:tag)
     */
    public String getFullName() {
        return repository + ":" + tag;
    }

    /**
     * 이미지 ID의 짧은 형태 반환 (Docker CLI와 유사)
     */
    public String getShortImageId() {
        return imageId.length() > 12 ? imageId.substring(0, 12) : imageId;
    }

    /**
     * latest 태그인지 확인
     */
    public boolean isLatest() {
        return "latest".equals(tag);
    }

    /**
     * 특정 이름과 매치되는지 확인
     */
    public boolean matches(String imageName) {
        // nginx, nginx:latest, nginx:1.21 등과 매치
        if (imageName.contains(":")) {
            return getFullName().equals(imageName);
        } else {
            return repository.equals(imageName);
        }
    }
}