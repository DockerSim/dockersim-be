package com.dockersim.repository;

import com.dockersim.domain.ContainerStatus;
import com.dockersim.domain.DockerContainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerContainerRepository extends JpaRepository<DockerContainer, Long> {

    boolean existsByImageIdAndStatus(String imageId, ContainerStatus status);
}
