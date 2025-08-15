package com.dockersim.service.image;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import java.util.List;

public interface DockerOfficeImageService {

    void loadAllFromJson();

    DockerOfficeImage findByNameForImageService(String name);

    DockerOfficeImageResponse findByName(String name);

    List<DockerOfficeImageResponse> getAllImages(int offset, int limit);
}