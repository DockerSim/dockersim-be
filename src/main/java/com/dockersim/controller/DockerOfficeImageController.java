package com.dockersim.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.service.image.DockerOfficeImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/officeimage")
@RequiredArgsConstructor
@Tag(name = "도커 공식 이미지 API", description = "도커 공식 이미지 이름/전체 조회 API")
public class DockerOfficeImageController {

	private final DockerOfficeImageService service;

	/**
	 * Docker 공식 Image를 이름(name)으로 조회합니다.
	 *
	 * @param name Image 이름
	 * @return 조회된 도커 이미지 정보
	 */
	@Operation(summary = "Docker 공식 이미지 조회",
		description = "Docker 공식 Image를 이름(name)으로 조회합니다.")
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<DockerOfficeImageResponse>>> findByName(
		@Parameter(description = "조회할 도커 이미지 이름(name)", required = true) @RequestParam String name
	) {
		return ResponseEntity.ok(ApiResponse.success(service.findAllByName(name)));
	}

	/**
	 * Docker 공식 Image를 name과 tag로 검색합니다.
	 *
	 * @param name Image 이름
	 * @param tag  Image 태그
	 */
	@Operation(summary = "Docker 공식 이미지 조회",
		description = "Docker 공식 이미지를 Image 이름(name)과 태그(tag)로 조회합니다.")
	@GetMapping("/search/detail")
	public ResponseEntity<ApiResponse<DockerOfficeImageResponse>> findByNameAndTag(
		@Parameter(description = "조회할 도커 이미지 이름(name)", required = true) @RequestParam String name,
		@Parameter(description = "조회할 도커 이미지 태그(tag)", required = true) @RequestParam String tag
	) {
		return ResponseEntity.ok(ApiResponse.success(service.findByNameAndTag(name, tag)));
	}

	/**
	 * Docker 공식 이미지 전체 조회
	 */
	@Operation(summary = "Docker 공식 Image 전체 조회",
		description = "모든 Docker 공식 Image를 조회합니다.")
	@GetMapping("/list")
	public ResponseEntity<ApiResponse<List<DockerOfficeImageResponse>>> getAllImages() {
		List<DockerOfficeImageResponse> list = service.getAllImages();
		return ResponseEntity.ok(ApiResponse.success(list));
	}
}