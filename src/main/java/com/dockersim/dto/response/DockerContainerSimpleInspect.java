package com.dockersim.dto.response;

import com.dockersim.domain.ContainerNetwork;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DockerContainerSimpleInspect {
	private String id;
	private String name;

	public static DockerContainerSimpleInspect from(ContainerNetwork containerNetwork) {
		return new DockerContainerSimpleInspect(containerNetwork.getContainer().getHexId(),
			containerNetwork.getContainer().getName());
	}
}
