package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.config.auth.CurrentUser;
import com.dockersim.dto.request.DockerFileRequest;
import com.dockersim.dto.response.DockerFileResponse;
import com.dockersim.service.dockerfile.DockerFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dockerfiles")
@RequiredArgsConstructor
public class DockerFileController {

    private final DockerFileService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DockerFileResponse>> createDockerFile(
            @CurrentUser SimulationUserPrincipal principal,
            @RequestBody DockerFileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.createDockerFile(principal, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DockerFileResponse>> getDockerFile(
            @CurrentUser SimulationUserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getDockerFileInfo(principal, id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DockerFileResponse>>> getAllDockerFiles(
            @CurrentUser SimulationUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getDockerFileSummary(principal)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DockerFileResponse>> updateDockerFile(
            @CurrentUser SimulationUserPrincipal principal,
            @PathVariable Long id,
            @RequestBody DockerFileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.updateDockerFile(principal, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDockerFile(
            @CurrentUser SimulationUserPrincipal principal,
            @PathVariable Long id
    ) {
        service.deleteDockerfile(principal, id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
