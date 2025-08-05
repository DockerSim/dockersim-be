package com.dockersim.repository;

import com.dockersim.domain.DockerOfficeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DockerOfficeImageRepository extends JpaRepository<DockerOfficeImage, Long> {
    Optional<DockerOfficeImage> findByName(String name);
}
