package com.dockersim.service;

import com.dockersim.dto.DockerImageResponse;

import java.util.List;

public interface DockerOfficeImageService {
    void loadAllFromJson();
    DockerImageResponse findByName(String name);
    List<DockerImageResponse> getAllImages(int offset, int limit);
}