package com.dockersim.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.domain.ImageLocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerImageResponse {

	private List<String> console;

	// common
	private String hexId;
	private String shortHexId;
	private String namespace;
	private String name;
	private String tag;
	private ImageLocation location;
	private List<String> layer;
	private LocalDateTime createdAt;

	// only office image
	private String description;
	private int starCount;
	private long pullCount;
	private String logoUrl;

	/*
	target:
		build

	 */
	public static DockerImageResponse from(DockerImage image, List<String> console) {
		return DockerImageResponse.builder()
			.console(console)
			.hexId(image.getHexId())
			.shortHexId(image.getShortHexId())
			.namespace(image.getNamespace())
			.name(image.getName())
			.tag(image.getTag())
			.location(image.getLocation())
			.layer(image.getLayers())
			.createdAt(image.getCreatedAt())
			.build();
	}

	/**
	 * 도커 공식 이미지 컨트롤러 DTO
	 * <p>
	 * location: 공식 저장소 조회이므로 항상 HUB, layer: 공식 이미지명으로 대체.
	 */
	public static DockerImageResponse from(DockerOfficeImage officeImage, List<String> console) {
		return DockerImageResponse.builder()
			.console(console)
			.hexId(officeImage.getHexId())
			.shortHexId(officeImage.getShortHexId())
			.namespace(officeImage.getNamespace())
			.name(officeImage.getName())
			.tag(officeImage.getTag())
			.layer(officeImage.getName())
			.location(ImageLocation.HUB)
			.createdAt(officeImage.getLastUpdated())
			.description(officeImage.getDescription())
			.starCount(officeImage.getStarCount())
			.pullCount(officeImage.getPullCount())
			.logoUrl(officeImage.getLogoUrl())
			.build();
	}
}