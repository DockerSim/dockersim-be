package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.service.image.DockerOfficeImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/officeimage")
@RequiredArgsConstructor
@Tag(name = "도커 공식 이미지 API", description = "도커 공식 이미지 이름/전체 조회 API")
public class DockerOfficeImageController {

    private final DockerOfficeImageService service;


    /**
     * Docker 공식 이미지를 이미지 이름(repository name)으로 조회합니다.
     *
     * @param repositoryName 조회할 도커 이미지 이름(repository name)
     * @return 조회된 도커 이미지 정보
     */
    @Operation(summary = "Docker 공식 이미지 조회",
        description = "Docker 공식 이미지를 이미지 이름(repository name)으로 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DockerOfficeImageResponse>>> findByName(
        @Parameter(description = "조회할 도커 이미지 이름(repository name)", required = true) @RequestParam String repositoryName
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findAllByName(repositoryName)));
    }


    /**
     * Docker 공식 이미지 전체 조회
     *
     * @param offset 시작 인덱스(기본 0)
     * @param limit  가져올 개수(기본 20)
     * @return 조회된 도커 이미지 정보
     */
    @Operation(summary = "Docker 공식 이미지 전체 조회",
        description = "offset, limit 파라미터로 Docker 공식 이미지를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DockerOfficeImageResponse>>> getAllImages(
        @Parameter(description = "시작 인덱스(기본 0)") @RequestParam(defaultValue = "0") int offset,
        @Parameter(description = "가져올 개수(기본 20)") @RequestParam(defaultValue = "20") int limit
    ) {
        List<DockerOfficeImageResponse> list = service.getAllImages(offset, limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}