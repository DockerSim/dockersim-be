package com.dockersim.controller;

import com.dockersim.common.ApiResponse;
import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.config.auth.CurrentUser;
import com.dockersim.dto.request.DockerFileRequest;
import com.dockersim.dto.request.DockerfileFeedbackRequest;
import com.dockersim.dto.response.DockerFileResponse;
import com.dockersim.dto.response.DockerfileFeedbackResponse;
import com.dockersim.service.dockerfile.DockerFileService;
import com.dockersim.service.dockerfile.DockerfileAnalysisService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dockerfiles")
@RequiredArgsConstructor
public class DockerFileController {

    private final DockerFileService service;
    private final DockerfileAnalysisService analysisService;

    @PostMapping
    public ResponseEntity<ApiResponse<DockerFileResponse>> createDockerFile(
        @CurrentUser SimulationUserPrincipal principal,
        @RequestBody DockerFileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.create(principal, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DockerFileResponse>> getDockerFile(
        @CurrentUser SimulationUserPrincipal principal,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.get(principal, id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DockerFileResponse>>> getAllDockerFiles(
        @CurrentUser SimulationUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(principal)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DockerFileResponse>> updateDockerFile(
        @CurrentUser SimulationUserPrincipal principal,
        @PathVariable Long id,
        @RequestBody DockerFileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.update(principal, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDockerFile(
        @CurrentUser SimulationUserPrincipal principal,
        @PathVariable Long id
    ) {
        service.delete(principal, id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<DockerfileFeedbackResponse>> getDockerfileFeedback(
        @Valid @RequestBody DockerfileFeedbackRequest request
    ) {
        DockerfileFeedbackResponse response = analysisService.analyzeDockerfile(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
