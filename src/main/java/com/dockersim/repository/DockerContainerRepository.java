package com.dockersim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dockersim.domain.DockerContainer;

public interface DockerContainerRepository extends JpaRepository<DockerContainer, Long> {

}
