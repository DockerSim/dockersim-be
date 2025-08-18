package com.dockersim.repository;

import com.dockersim.domain.DockerImage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerImageRepository extends JpaRepository<DockerImage, Long> {

    Optional<DockerImage> findByNameAndTag(String repositoryName, String tag);
}