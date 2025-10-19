package com.dockersim.service.network;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerNetworkRepository extends JpaRepository<DockerNetwork, Long> {
}
