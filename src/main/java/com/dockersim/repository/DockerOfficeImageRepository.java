package com.dockersim.repository;

import com.dockersim.domain.DockerOfficeImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerOfficeImageRepository extends JpaRepository<DockerOfficeImage, Long> {

    List<DockerOfficeImage> findAllByName(String name);

    Optional<DockerOfficeImage> findByNameAndTag(String repositoryName, String tag);
}
