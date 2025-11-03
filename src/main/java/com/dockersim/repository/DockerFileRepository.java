package com.dockersim.repository;

import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerFileRepository extends JpaRepository<DockerFile, Long> {

    Optional<DockerFile> findByIdAndUser(Long id, User user);

    Optional<DockerFile> findByPathAndUser(String path, User user);

    List<DockerFile> findAllByUser(User user);
}
