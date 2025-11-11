package com.dockersim.service.image;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.DockerOfficeImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.dto.DockerImageJson;
import com.dockersim.dto.response.DockerOfficeImageResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.DockerImageErrorCode;
import com.dockersim.repository.DockerImageRepository;
import com.dockersim.repository.DockerOfficeImageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Import Optional
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class DockerOfficeImageServiceImpl implements DockerOfficeImageService {

    private final DockerOfficeImageRepository officeImageRepository;
    private final DockerImageRepository imageRepository;
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
                        log.debug("Saving DockerOfficeImage: name={}, tag={}, hexId={}", image.getName(), image.getTag(), image.getHexId());
                        officeImageRepository.save(image);

                        imageRepository.save(DockerImage.from(image, ImageLocation.HUB));
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
    public Optional<DockerOfficeImageResponse> findByNameAndTag(String name, String tag) {
        log.debug("Attempting to find DockerOfficeImage by name={} and tag={}", name, tag);
        Optional<DockerOfficeImageResponse> result = officeImageRepository.findByNameAndTag(name, tag)
                .map(DockerOfficeImageResponse::from);
        if (result.isPresent()) {
            log.debug("Found DockerOfficeImage: name={}, tag={}", name, tag);
        } else {
            log.debug("DockerOfficeImage not found for name={} and tag={}", name, tag);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<DockerOfficeImageResponse> findAllByName(String name) {
        log.debug("Attempting to find all DockerOfficeImage by name={}", name);
        List<DockerOfficeImage> images = officeImageRepository.findAllByName(name);

        if (images.isEmpty()) {
            log.debug("No DockerOfficeImage found for name={}", name);
            throw new BusinessException(DockerImageErrorCode.OFFICE_IMAGE_NOT_FOUND, name);
        }
        log.debug("Found {} DockerOfficeImages for name={}", images.size(), name);
        return images.stream()
                .map(DockerOfficeImageResponse::from)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<DockerOfficeImageResponse> getAllImages() {
        log.debug("Attempting to get all DockerOfficeImages");
        List<DockerOfficeImage> allImages = officeImageRepository.findAll();
        Map<String, DockerOfficeImage> uniqueImages = new LinkedHashMap<>();
        for (DockerOfficeImage image : allImages) {
            uniqueImages.putIfAbsent(image.getName(), image);
        }
        log.debug("Found {} unique DockerOfficeImages", uniqueImages.size());
        return uniqueImages.values().stream()
                .map(DockerOfficeImageResponse::from)
                .collect(Collectors.toList());
    }
}
