package com.dockersim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Docker Image Object
 */
@Entity
@Getter
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DockerImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_id", nullable = false, unique = true)
    private String imageId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String namespace;
    @Column(nullable = false)
    private String tags;
    private LocalDate created;
    private String logoUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;


    /**
     * 공식 이미지로부터 표준화된 이미지 엔티티 생성
     *
     * @param office
     */
    public static DockerImage fromDockerOfficeImage(DockerOfficeImage office) {
        return DockerImage.builder()
            .imageId(office.getImageId())
            .name(office.getName())
            .namespace(office.getNamespace())
            .created(office.getLastUpdated())
            .logoUrl(office.getLogoUrl())
            .tags(office.getTags())
            .build();
    }

    /**
     * 전체 이미지명 반환 (namespace:tag)
     */
    public String getFullName() {
        return namespace + ":" + tags;
    }

    /**
     * 단축형 이미지 ID 반환
     */
    public String getShortImageId() {
        return imageId.length() > 12 ? imageId.substring(0, 12) : imageId;
    }

    /**
     * latest 태그인지 확인
     */
    public boolean isLatest() {
        return "latest".equals(tags);
    }

    /**
     * 특정 이름과 매치되는지 확인
     */
    public boolean matches(String imageNameOrId) {
        if (imageNameOrId.contains(":")) {
            return getFullName().equals(imageNameOrId);
        } else {
            return imageNameOrId.equals(getName()) || imageNameOrId.equals(getImageId());
        }
    }
}
