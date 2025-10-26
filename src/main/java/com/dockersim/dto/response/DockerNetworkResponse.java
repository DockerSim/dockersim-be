package com.dockersim.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dockersim.domain.ConnectState;
import com.dockersim.domain.DockerNetwork;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DockerNetworkResponse {
	private List<String> console;

	private Long id;
	private String shortHexId;
	private String name;

	private LocalDateTime createdAt;

	/*
	네트워크에 연결/해제된 컨테이너 상태
	NONE이면 containerId 필드는 무시
	 */
	private ConnectState connectState;
	private Long containerId;

	public static DockerNetworkResponse from(DockerNetwork network, List<String> console) {
		return DockerNetworkResponse.builder()
			.console(console)
			.id(network.getId())
			.shortHexId(network.getShortHexId())
			.name(network.getName())
			.createdAt(network.getCreatedAt())
			.connectState(ConnectState.NONE)
			.build();
	}

	public static DockerNetworkResponse from(
		DockerNetwork network, List<String> console,
		boolean isConnect, Long containerId
	) {
		return DockerNetworkResponse.from(network, console).toBuilder()
			.connectState(isConnect ? ConnectState.CONNECTED : ConnectState.DISCONNECTED)
			.containerId(containerId)
			.build();
	}
}
