package com.dockersim.controller;

import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.service.image.DockerOfficeImageService;
import com.dockersim.web.ApiResponse;
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
public class DockerImageController {

    private final DockerOfficeImageService service;

    @Operation(summary = "Docker 이미지 이름으로 조회",
        description = "이름(name)만으로 Docker 이미지를 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<DockerOfficeImageResponse>> findByName(
        @Parameter(description = "조회할 Docker 이미지 이름", required = true)
        @RequestParam String name
    ) {
        DockerOfficeImageResponse dto = service.findByName(name);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @Operation(summary = "전체 Docker 이미지 조회",
        description = "offset: 시작 인덱스(기본 0), limit: 가져올 개수(기본 20)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DockerOfficeImageResponse>>> getAllImages(
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit
    ) {
        List<DockerOfficeImageResponse> list = service.getAllImages(offset, limit);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}