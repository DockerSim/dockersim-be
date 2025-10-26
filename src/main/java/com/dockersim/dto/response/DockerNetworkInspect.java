package com.dockersim.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dockersim.domain.DockerNetwork;

import lombok.Builder;

@Builder
public class DockerNetworkInspect {
	private String name;
	private String id;
	private LocalDateTime created;
	private List<DockerContainerSimpleInspect> containers;

	public static DockerNetworkInspect from(DockerNetwork network, List<DockerContainerSimpleInspect> containers) {
		return DockerNetworkInspect.builder()
			.name(network.getName())
			.id(network.getHexId())
			.created(network.getCreatedAt())
			.containers(containers)
			.build();
	}
}
