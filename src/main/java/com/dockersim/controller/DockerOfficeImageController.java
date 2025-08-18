package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.service.image.DockerOfficeImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class DockerOfficeImageController {

    private final DockerOfficeImageService service;

    @Operation(summary = "Docker 공식 이미지 이름으로 조회",
        description = "이름(repositoryName)만으로 Docker 공식 이미지들을 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DockerOfficeImageResponse>>> findByName(
        @Parameter(description = "조회할 Docker Image의 repository name", required = true)
        @RequestParam String repositoryName
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findAllByName(repositoryName)));
    }

    @Operation(summary = "전체 Docker Image 조회",
        description = "offset: 시작 인덱스(기본 0), limit: 가져올 개수(기본 20)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DockerOfficeImageResponse>>> getAllImages(
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit
    ) {
        List<DockerOfficeImageResponse> list = service.getAllImages(offset, limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}