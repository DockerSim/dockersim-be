package com.dockersim.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dockersim.domain.DockerVolume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerVolumeResponse {

	private List<String> console;

	private LocalDateTime createAt;
	private String mountPoint;
	private String name;

	private boolean anonymous;

	public static DockerVolumeResponse from(DockerVolume volume, List<String> console) {
		return DockerVolumeResponse.builder()
			.console(console)
			.mountPoint(volume.getMountPoint())
			.name(volume.getName())
			.anonymous(volume.isAnonymous())
			.build();
	}
}
