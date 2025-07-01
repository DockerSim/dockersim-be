// 이 클래스는 Docker 이미지 정보를 저장하는 JPA 엔티티입니다.
// 주요 필드:
// - id : 엔티티 ID
// - imageId : Docker 이미지 ID (sha256 해시)
// - name : 이미지 이름 (repository)
// - tag : 이미지 태그
// - size : 이미지 크기 (바이트)
// - createdAt : 생성 시간

package com.dockersim.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_id", unique = true, nullable = false)
    private String imageId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "size")
    private Long size;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 기본 생성자
    protected Image() {
    }

    // 생성자
    public Image(String imageId, String name, String tag, Long size) {
        this.imageId = imageId;
        this.name = name;
        this.tag = tag;
        this.size = size;
        this.createdAt = LocalDateTime.now();
    }

    // Getter/Setter 메서드들
    public Long getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 비즈니스 메서드
    public String getFullName() {
        return name + ":" + tag;
    }
}