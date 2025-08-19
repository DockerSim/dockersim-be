package com.dockersim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docker_images")
@Getter
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


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private String tag;

    public static DockerImage from(DockerOfficeImage officeImage) {
        return DockerImage.builder()
            .imageId(officeImage.getImageId())
            .name(officeImage.getName())
            .namespace(officeImage.getNamespace())
            .createdAt(officeImage.getLastUpdated())
            .tag(officeImage.getTag())
            .build();

    }


    public String getFullName() {
        return namespace + "/" + name;
    }

    public String getFullNameWithTag(String tag) {
        return getFullName() + ":" + tag;
    }
}