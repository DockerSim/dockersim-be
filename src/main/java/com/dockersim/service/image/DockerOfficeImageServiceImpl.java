package com.dockersim.service.image;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerOfficeImageRepository;
import com.dockersim.service.DockerImageJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DockerOfficeImageServiceImpl implements DockerOfficeImageService {

    private final DockerOfficeImageRepository repo;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public void loadAllFromJson() {
        String path = "static/data/docker_images.json";
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            List<DockerImageJson> list = mapper.readValue(is,
                new TypeReference<List<DockerImageJson>>() {
                });
            for (DockerImageJson jsonImage : list) {
                repo.save(DockerOfficeImage.fromJson(jsonImage));
            }
        } catch (Exception e) {
            throw new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_DATA_LOAD_FAIL);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public DockerOfficeImage findByNameForImageService(String name) {
        return repo.findByName(name).orElse(null);
    }


    @Override
    @Transactional(readOnly = true)
    public DockerOfficeImageResponse findByName(String name) {
        DockerOfficeImage image = repo.findByName(name)
            .orElseThrow(() ->
                new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND, name)
            );
        return DockerOfficeImageResponse.fromDockerOfficeImage(image);
    }


    @Override
    @Transactional(readOnly = true)
    public List<DockerOfficeImageResponse> getAllImages(int offset, int limit) {
        List<DockerOfficeImageResponse> all = repo.findAll().stream()
            .map(DockerOfficeImageResponse::fromDockerOfficeImage)
            .collect(Collectors.toList());

        int start = Math.max(0, offset);
        int end = Math.min(all.size(), offset + limit);
        if (start >= all.size()) {
            return Collections.emptyList();
        }
        return all.subList(start, end);
    }
}
