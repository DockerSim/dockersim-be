package com.dockersim.repository;

import com.dockersim.domain.DockerImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DockerImageRepository extends JpaRepository<DockerImage, Long> {
    Optional<DockerImage> findByName(String name);
}
