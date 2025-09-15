package com.dockersim.service.image;

import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.dto.DockerImageJson;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerOfficeImageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class DockerOfficeImageServiceImpl implements DockerOfficeImageService {

    private final DockerOfficeImageRepository repo;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public void loadAllFromJson() {
        String resourcePath = "/static/data/docker_images.json";
        log.info("Loading docker images from {}", resourcePath);

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_DATA_LOAD_FAIL,
                    resourcePath);
            }

            List<DockerImageJson> list = mapper.readValue(is,
                new TypeReference<List<DockerImageJson>>() {
                });

            log.info("Found {} images in JSON file", list.size());
            int savedCount = 0;

            for (DockerImageJson jsonImage : list) {
                if (jsonImage.getTags() == null || jsonImage.getTags().isEmpty()) {
                    log.warn("Skipping image {} because it has no tags", jsonImage.getName());
                    continue;
                }

                try {
                    for (String tag : jsonImage.getTags()) {
                        DockerOfficeImage image = DockerOfficeImage.from(jsonImage, tag);
                        repo.save(image);
                        ++savedCount;
                    }
                } catch (Exception e) {
                    log.error("Failed to process image {}: {}", jsonImage.getName(),
                        e.getMessage());
                }
            }

            log.info("Successfully saved {} image entries", savedCount);
        } catch (Exception e) {
            log.error("Unexpected error while loading images: {}", e.getMessage());
            throw new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_CRITICAL_ERROR);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public DockerOfficeImageResponse findByNameAndTag(String name, String tag) {
        return DockerOfficeImageResponse.from(repo.findByNameAndTag(name, tag).orElseThrow(
                ()->new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND, name + ":" + tag)
        ));
    }

    @Transactional(readOnly = true)
    public List<DockerOfficeImageResponse> findAllByName(String name) {

        List<DockerOfficeImage> images = repo.findAllByName(name);

        if (images.isEmpty()) {
            throw new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND, name);
        }
        return images.stream()
            .map(DockerOfficeImageResponse::from)
            .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<DockerOfficeImageResponse> getAllImages() {
        return repo.findAll().stream()
            .map(DockerOfficeImageResponse::from)
            .collect(Collectors.toList());
    }
}
