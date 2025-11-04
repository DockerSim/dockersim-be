package com.dockersim.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dockersim.domain.DockerFile;
import com.dockersim.domain.User;

public interface DockerFileRepository extends JpaRepository<DockerFile, Long> {

	Optional<DockerFile> findByIdAndUser(Long id, User user);

	Optional<DockerFile> findByPathAndUser(String path, User user);

	List<DockerFile> findAllByUser(User user);

	boolean existsByPath(String path);
}
